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
import net.minepact.core.global.commands.punishment.helper.message.getPunishmentMessage
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerKickEvent

class PlayerJoinHandler : SimpleEventHandler<PlayerJoinEvent>() {
    override fun handle(context: EventContext<PlayerJoinEvent>) {
        val event: PlayerJoinEvent = context.event
        event.joinMessage(MiniMessage.miniMessage().deserialize("<dark_grey>[<green><bold>+</bold><dark_grey>] <grey>${event.player.name}"))
        if (PlayerRepository.findByUUID(event.player.uniqueId).get() == null) {
            val newData = PlayerData(
                uuid = event.player.uniqueId,
                name = event.player.name,
                discordId = "",
                firstJoined = System.currentTimeMillis(),
                lastSeen = System.currentTimeMillis()
            )
            PlayerRepository.insert(newData)

            if (!PlayerRegistry.playersByUUID.containsKey(event.player.uniqueId)) PlayerRegistry.register(Player(newData, true))
        } else {
            val data = PlayerRepository.findByUUID(event.player.uniqueId).get() ?: return
            if (!PlayerRegistry.playersByUUID.containsKey(event.player.uniqueId)) PlayerRegistry.register(Player(data, true))
        }

        PlayerRegistry.get(event.player.uniqueId).thenAccept { it.online = true }
        PunishmentRepository.findByTarget(event.player.name).thenAccept { punishments ->
            val activeBan = punishments.firstOrNull { punishment ->
                        punishment.type == PunishmentType.BAN
                        && !punishment.reverted
                        && System.currentTimeMillis() < punishment.expiresAt
                        && punishment.targetServers.contains(Main.SERVER.info.uuid)
            }

            if (activeBan != null) {
                event.player.kick(
                    MiniMessage.miniMessage().deserialize(getPunishmentMessage(activeBan, AnnouncementModifier.SILENT)),
                    PlayerKickEvent.Cause.BANNED
                )
            }
        }

        if (Main.MAIN_CONFIG.spawn.teleportOnJoin) {
            event.player.teleport(Location(
                Bukkit.getWorld(Main.MAIN_CONFIG.spawn.world),
                Main.MAIN_CONFIG.spawn.x,
                Main.MAIN_CONFIG.spawn.y,
                Main.MAIN_CONFIG.spawn.z,
                Main.MAIN_CONFIG.spawn.yaw,
                Main.MAIN_CONFIG.spawn.pitch
            ))
        }
    }
}