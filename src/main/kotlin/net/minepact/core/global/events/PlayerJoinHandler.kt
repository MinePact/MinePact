package net.minepact.core.global.events

import net.kyori.adventure.text.minimessage.MiniMessage
import net.minepact.Main
import net.minepact.api.config.ConfigurationRegistry
import net.minepact.api.event.EventContext
import net.minepact.api.event.SimpleEventHandler
import net.minepact.api.misc.formatDate
import net.minepact.api.misc.formatDuration
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
            if (System.currentTimeMillis() > punishment.expiresAt!!) return
            event.player.kick(MiniMessage.miniMessage().deserialize(
                ConfigurationRegistry.get(PunishmentConfig::class).ban.kickMessage
                    .joinToString { "\n" }
                    .replace("{REASON}", punishment.reason)
                    .replace("{EXPIRES_AT}", formatDate(punishment.expiresAt))
                    .replace("{EXPIRES_IN}", formatDuration(punishment.expiresAt - System.currentTimeMillis()))
                    .replace("{TARGET}", punishment.targetName)
                    .replace("{ISSUER}", punishment.issuerName)

            ),
                PlayerKickEvent.Cause.BANNED
            )
        } }

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