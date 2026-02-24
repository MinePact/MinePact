package net.minepact.core.global.events

import net.kyori.adventure.text.minimessage.MiniMessage
import net.minepact.Main
import net.minepact.api.data.repository.PlayerRepository
import net.minepact.api.data.repository.PunishmentRepository
import net.minepact.api.event.EventContext
import net.minepact.api.event.SimpleEventHandler
import net.minepact.api.player.Player
import net.minepact.api.player.PlayerData
import net.minepact.api.player.PlayerRegistry
import net.minepact.api.punishment.PunishmentType
import net.minepact.api.punishment.modifier.AnnouncementModifier
import net.minepact.core.global.commands.staff.punishment.helper.message.getPunishmentMessage
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerKickEvent

class PlayerJoinHandler : SimpleEventHandler<PlayerJoinEvent>() {
    override fun handle(context: EventContext<PlayerJoinEvent>) {
        val event: PlayerJoinEvent = context.event
        val player = event.player
        val uuid = player.uniqueId
        val currentIp = player.address?.address?.hostAddress

        event.joinMessage(MiniMessage.miniMessage().deserialize("<dark_grey>[<green><bold>+</bold><dark_grey>] <grey>${player.name}"))

        val optional = PlayerRepository.findByUUID(uuid)
        val data: PlayerData

        if (optional.get() == null) {
            data = PlayerData(
                uuid = uuid,
                name = player.name,
                ipHistory = currentIp?.let { listOf(it) } ?: listOf(),
                discordId = "",
                firstJoined = System.currentTimeMillis(),
                lastSeen = System.currentTimeMillis()
            )
            PlayerRepository.insert(data)
        } else {
            data = optional.get() ?: return
            if (currentIp != null && !data.ipHistory.contains(currentIp)) {
                val updatedIps = data.ipHistory + currentIp
                data.ipHistory = updatedIps
                PlayerRepository.insert(data)
            }

            data.lastSeen = System.currentTimeMillis()
            PlayerRepository.insert(data)
        }

        if (!PlayerRegistry.playersByUUID.containsKey(uuid)) {
            PlayerRegistry.register(Player(data, true))
        }

        PlayerRegistry.get(uuid).thenAccept { it.online = true }

        PunishmentRepository.findByTarget(player.uniqueId).thenAccept { punishments ->
            val activeBan = punishments.firstOrNull { punishment ->
                punishment.type == PunishmentType.BAN &&
                        !punishment.reverted &&
                        System.currentTimeMillis() < punishment.expiresAt &&
                        punishment.servers.contains(Main.SERVER.info.uuid)
            }

            if (activeBan != null) {
                player.kick(
                    MiniMessage.miniMessage().deserialize(getPunishmentMessage(activeBan, AnnouncementModifier.SILENT)),
                    PlayerKickEvent.Cause.BANNED
                )
            }
        }

        if (Main.MAIN_CONFIG.spawn.teleportOnJoin) {
            player.teleport(
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