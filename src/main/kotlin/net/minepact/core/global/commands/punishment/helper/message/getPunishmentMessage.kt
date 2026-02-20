package net.minepact.core.global.commands.punishment.helper.message

import net.minepact.api.config.ConfigurationRegistry
import net.minepact.api.misc.formatDate
import net.minepact.api.misc.formatDuration
import net.minepact.api.punishment.Punishment
import net.minepact.api.punishment.modifier.PunishmentModifier
import net.minepact.api.punishment.PunishmentType
import net.minepact.core.global.configs.PunishmentConfig

fun getPunishmentMessage(punishment: Punishment, announcement: PunishmentModifier): String {
    val config: PunishmentConfig = ConfigurationRegistry.get(PunishmentConfig::class)

    when (punishment.type) {
        PunishmentType.BAN -> {
            return config.ban.kickMessage.joinToString(separator = "\n")
                .replace("{REASON}", punishment.reason)
                .replace("{EXPIRES_AT}", formatDate(punishment.expiresAt))
                .replace("{EXPIRES_IN}", formatDuration(punishment.expiresAt - System.currentTimeMillis()))
                .replace("{TARGET}", punishment.targetName)
                .replace("{ISSUER}", punishment.issuerName)
                .replace("{BCST_MOD}", announcement.name)
        }
        PunishmentType.MUTE -> {
            return config.mute.muteMessage.joinToString(separator = "\n")
                .replace("{REASON}", punishment.reason)
                .replace("{EXPIRES_AT}", formatDate(punishment.expiresAt))
                .replace("{EXPIRES_IN}", if (punishment.expiresAt != Long.MIN_VALUE) formatDuration(punishment.expiresAt - System.currentTimeMillis()) else "Never")
                .replace("{TARGET}", punishment.targetName)
                .replace("{ISSUER}", punishment.issuerName)
                .replace("{BCST_MOD}", announcement.name)
        }
        PunishmentType.WARN -> {
            return config.warn.warnMessage.joinToString(separator = "\n")
                .replace("{REASON}", punishment.reason)
                .replace("{EXPIRES_AT}", formatDate(punishment.expiresAt))
                .replace("{EXPIRES_IN}", formatDuration(punishment.expiresAt - System.currentTimeMillis()))
                .replace("{TARGET}", punishment.targetName)
                .replace("{ISSUER}", punishment.issuerName)
                .replace("{BCST_MOD}", announcement.name)
        }
    }
}