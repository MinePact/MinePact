package net.minepact.core.global.events

import net.kyori.adventure.text.minimessage.MiniMessage
import net.minepact.Main
import net.minepact.api.data.repository.PlayerRepository
import net.minepact.api.data.repository.PunishmentRepository
import net.minepact.api.event.EventContext
import net.minepact.api.event.SimpleEventHandler
import net.minepact.api.math.Vector
import net.minepact.api.math.helper.vector.vec
import net.minepact.api.permissions.PermissionCache
import net.minepact.api.permissions.PermissionInjector
import net.minepact.api.permissions.graph.PermissionCompiler
import net.minepact.api.player.Player
import net.minepact.api.player.PlayerData
import net.minepact.api.player.PlayerRegistry
import net.minepact.api.punishment.PunishmentType
import net.minepact.api.punishment.modifier.AnnouncementModifier
import net.minepact.api.world.Position
import net.minepact.core.global.commands.staff.punishment.helper.message.getPunishmentMessage
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerKickEvent

class PlayerJoinHandler : SimpleEventHandler<PlayerJoinEvent>() {
    override fun handle(context: EventContext<PlayerJoinEvent>) {
        val event: PlayerJoinEvent = context.event
        val bukkitPlayer = event.player
        val uuid = bukkitPlayer.uniqueId
        val currentIp = bukkitPlayer.address?.address?.hostAddress

        event.joinMessage(MiniMessage.miniMessage().deserialize("<dark_grey>[<green><bold>+</bold><dark_grey>] <grey>${bukkitPlayer.name}"))

        PlayerRepository.findByUUID(uuid).thenCompose { existing ->
            if (existing == null) {
                val data = PlayerData(
                    uuid = uuid,
                    name = bukkitPlayer.name,
                    ipHistory = currentIp?.let { listOf(it) } ?: listOf(),
                    discordId = "",
                    firstJoined = System.currentTimeMillis(),
                    lastSeen = System.currentTimeMillis())
                PlayerRepository.insert(data).thenApply { data }
            } else {
                existing.lastSeen = System.currentTimeMillis()

                if (currentIp != null && !existing.ipHistory.contains(currentIp)) {
                    existing.ipHistory = existing.ipHistory + currentIp
                }
                PlayerRepository.insert(existing).thenApply { existing }
            }
        }.thenCompose { PlayerRegistry.get(uuid) }.thenAccept { player ->
            if (player == null) return@thenAccept

            player.online = true
            player.pos = Position(
                vector = vec(bukkitPlayer.x, bukkitPlayer.y, bukkitPlayer.z),
                yaw = bukkitPlayer.yaw,
                pitch = bukkitPlayer.pitch,
                world = bukkitPlayer.world.name
            )

            PermissionInjector.inject(bukkitPlayer)
            PermissionCache.put(
                player.data.uuid,
                PermissionCompiler.compile(player)
            )
        }

        PunishmentRepository.findByTarget(bukkitPlayer.uniqueId).thenAccept { punishments ->
            val activeBan = punishments.firstOrNull { punishment ->
                punishment.type == PunishmentType.BAN &&
                        !punishment.reverted &&
                        System.currentTimeMillis() < punishment.expiresAt &&
                        punishment.servers.contains(Main.SERVER.info.uuid)
            }

            if (activeBan != null) {
                bukkitPlayer.kick(
                    MiniMessage.miniMessage().deserialize(getPunishmentMessage(activeBan, AnnouncementModifier.SILENT)),
                    PlayerKickEvent.Cause.BANNED
                )
            }
        }

        if (Main.MAIN_CONFIG.spawn.teleportOnJoin) {
            bukkitPlayer.teleport(
                Location(
                    Bukkit.getWorld(Main.MAIN_CONFIG.spawn.world),
                    Main.MAIN_CONFIG.spawn.x,
                    Main.MAIN_CONFIG.spawn.y,
                    Main.MAIN_CONFIG.spawn.z,
                    Main.MAIN_CONFIG.spawn.yaw,
                    Main.MAIN_CONFIG.spawn.pitch
                )
            )
        }
    }
}