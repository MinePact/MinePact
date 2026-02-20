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
import net.minepact.api.punishment.modifier.PunishmentModifier
import net.minepact.api.punishment.PunishmentType
import net.minepact.api.punishment.modifier.AnnouncementModifier
import net.minepact.api.punishment.modifier.ScopeModifier
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
        val potentialPunishments = PunishmentRepository.findByTarget(event.player.name)

        potentialPunishments.get().forEach { punishment -> run {
                if ((System.currentTimeMillis() > punishment.expiresAt) && (punishment.expiresAt != Long.MIN_VALUE)) return@run
                if (punishment.type != PunishmentType.BAN) return@run
                if (event.player.hasPermission("minepact.punishments.bypass.${punishment.type.name.lowercase()}")) return@run

                event.player.kick(MiniMessage.miniMessage().deserialize(getPunishmentMessage(punishment, AnnouncementModifier.SILENT)), PlayerKickEvent.Cause.BANNED)
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