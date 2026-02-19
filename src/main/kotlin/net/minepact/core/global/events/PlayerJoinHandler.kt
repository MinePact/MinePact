package net.minepact.core.global.events

import net.kyori.adventure.text.minimessage.MiniMessage
import net.minepact.Main
import net.minepact.api.config.ConfigurationRegistry
import net.minepact.api.event.EventContext
import net.minepact.api.event.SimpleEventHandler
import net.minepact.api.misc.formatDate
import net.minepact.api.misc.formatDuration
import net.minepact.api.punishment.PunishmentModifiers
import net.minepact.api.punishment.PunishmentType
import net.minepact.core.global.commands.punishment.helper.message.getPunishmentMessage
import net.minepact.core.global.configs.PunishmentConfig
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerKickEvent
import java.text.SimpleDateFormat

class PlayerJoinHandler : SimpleEventHandler<PlayerJoinEvent>() {
    override fun handle(context: EventContext<PlayerJoinEvent>) {
        val event: PlayerJoinEvent = context.event
        event.joinMessage(MiniMessage.miniMessage().deserialize("<dark_grey>[<green><bold>+</bold><dark_grey>] <grey>${event.player.name}"))

        val PUNISHMENT_REPOSITORY = Main.PUNISHMENT_REPOSITORY
        val potentialPunishments = PUNISHMENT_REPOSITORY.findByTarget(event.player.name)

        potentialPunishments.get().forEach { punishment -> run {
                if ((System.currentTimeMillis() > punishment.expiresAt) && (punishment.expiresAt != Long.MIN_VALUE)) return@run
                if (punishment.type != PunishmentType.BAN) return@run
                if (event.player.hasPermission("minepact.punishments.bypass.${punishment.type.name.lowercase()}")) return@run

                event.player.kick(MiniMessage.miniMessage().deserialize(getPunishmentMessage(punishment, PunishmentModifiers.GLOBAL)), PlayerKickEvent.Cause.BANNED)
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