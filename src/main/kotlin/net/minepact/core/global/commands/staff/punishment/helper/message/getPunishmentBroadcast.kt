package net.minepact.core.global.commands.staff.punishment.helper.message

import net.minepact.api.config.ConfigurationRegistry
import net.minepact.api.misc.formatDate
import net.minepact.api.misc.formatDuration
import net.minepact.api.player.Player
import net.minepact.api.player.PlayerRegistry
import net.minepact.api.punishment.Punishment
import net.minepact.api.punishment.PunishmentType
import net.minepact.api.punishment.modifier.AnnouncementModifier
import net.minepact.core.global.configs.PunishmentConfig

fun getPunishmentBroadcast(punishment: Punishment, announcement: AnnouncementModifier): String {
    val config: PunishmentConfig = ConfigurationRegistry.get(PunishmentConfig::class)

    val issuer: Player = PlayerRegistry.get(punishment.issuer).get()
    val target: Player = PlayerRegistry.get(punishment.target).get()


    when (punishment.type) {
        PunishmentType.IP_BAN -> {
            return config.ipBan.announcementMessage
                .replace("{REASON}", punishment.reason)
                .replace("{EXPIRES_AT}", formatDate(punishment.expiresAt))
                .replace("{EXPIRES_IN}", formatDuration(punishment.expiresAt - System.currentTimeMillis()))
                .replace("{TARGET}", target.data.name)
                .replace("{ISSUER}", issuer.data.name)
                .replace("{BCST_MOD}", announcement.value)
        }
        PunishmentType.BAN -> {
            return config.ban.announcementMessage
                .replace("{REASON}", punishment.reason)
                .replace("{EXPIRES_AT}", formatDate(punishment.expiresAt))
                .replace("{EXPIRES_IN}", formatDuration(punishment.expiresAt - System.currentTimeMillis()))
                .replace("{TARGET}", target.data.name)
                .replace("{ISSUER}", issuer.data.name)
                .replace("{BCST_MOD}", announcement.value)
        }
        PunishmentType.MUTE -> {
            return config.mute.announcementMessage
                .replace("{REASON}", punishment.reason)
                .replace("{EXPIRES_AT}", formatDate(punishment.expiresAt))
                .replace("{EXPIRES_IN}", formatDuration(punishment.expiresAt - System.currentTimeMillis()))
                .replace("{TARGET}", target.data.name)
                .replace("{ISSUER}", issuer.data.name)
                .replace("{BCST_MOD}", announcement.value)
        }
        PunishmentType.WARN -> {
            return config.warn.announcementMessage
                .replace("{REASON}", punishment.reason)
                .replace("{EXPIRES_AT}", formatDate(punishment.expiresAt))
                .replace("{EXPIRES_IN}", formatDuration(punishment.expiresAt - System.currentTimeMillis()))
                .replace("{TARGET}", target.data.name)
                .replace("{ISSUER}", issuer.data.name)
                .replace("{BCST_MOD}", announcement.value)
        }
        PunishmentType.KICK -> {
            return config.kick.announcementMessage
                .replace("{REASON}", punishment.reason)
                .replace("{TARGET}", target.data.name)
                .replace("{ISSUER}", issuer.data.name)
                .replace("{BCST_MOD}", announcement.value)
        }
    }
}