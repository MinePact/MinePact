package net.minepact.core.global.commands.punishment.helper.message

import net.minepact.api.config.ConfigurationRegistry
import net.minepact.api.misc.formatDate
import net.minepact.api.misc.formatDuration
import net.minepact.api.punishment.Punishment
import net.minepact.core.global.configs.PunishmentConfig

fun getMuteAttemptMessage(punishment: Punishment): String {
    val config: PunishmentConfig = ConfigurationRegistry.get(PunishmentConfig::class)
    return config.mute.chatAttemptMessage.joinToString(separator = "\n")
        .replace("{EXPIRES_IN}", if (punishment.expiresAt != Long.MIN_VALUE) formatDuration(punishment.expiresAt - System.currentTimeMillis()) else "Never")
        .replace("{REASON}", punishment.reason)
        .replace("{EXPIRES_AT}", formatDate(punishment.expiresAt))
        .replace("{TARGET}", punishment.targetName)
        .replace("{ISSUER}", punishment.issuerName)
}