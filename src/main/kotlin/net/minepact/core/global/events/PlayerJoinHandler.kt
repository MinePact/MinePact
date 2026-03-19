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
                    discordId = "",
                    firstJoined = System.currentTimeMillis(),
                    lastSeen = System.currentTimeMillis())
                PlayerRepository.insert(data).thenApply { data }
            } else {
                existing.lastSeen = System.currentTimeMillis()
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

            if (player.isBanned()) player.disconnect(getPunishmentMessage(player.getActiveBan()!!, AnnouncementModifier.SILENT))
            else if (player.isIpBanned()) player.disconnect(getPunishmentMessage(player.getActiveIpBan()!!, AnnouncementModifier.SILENT))
        }
    }
}