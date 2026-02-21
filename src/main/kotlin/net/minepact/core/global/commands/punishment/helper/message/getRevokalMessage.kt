package net.minepact.core.global.commands.punishment.helper.message

import net.minepact.api.config.ConfigurationRegistry
import net.minepact.api.punishment.Punishment
import net.minepact.api.punishment.PunishmentType
import net.minepact.api.punishment.modifier.AnnouncementModifier
import net.minepact.core.global.configs.PunishmentConfig

fun getRevokalMessage(punishment: Punishment, announcement: AnnouncementModifier): String {
    val config: PunishmentConfig = ConfigurationRegistry.get(PunishmentConfig::class)

    when (punishment.type) {
        PunishmentType.IP_BAN -> {
            return config.ipBan.announcementRevokeMessage
                .replace("{REASON}", punishment.revertReason ?: "No reason provided.")
                .replace("{TARGET}", punishment.targetName)
                .replace("{ISSUER}", punishment.issuerName)
                .replace("{BCST_MOD}", announcement.value)
        }
        PunishmentType.BAN -> {
            return config.ban.announcementRevokeMessage
                .replace("{REASON}", punishment.revertReason ?: "No reason provided.")
                .replace("{TARGET}", punishment.targetName)
                .replace("{ISSUER}", punishment.issuerName)
                .replace("{BCST_MOD}", announcement.value)
        }
        PunishmentType.MUTE -> {
            return config.mute.announcementRevokeMessage
                .replace("{REASON}", punishment.revertReason ?: "No reason provided.")
                .replace("{TARGET}", punishment.targetName)
                .replace("{ISSUER}", punishment.issuerName)
                .replace("{BCST_MOD}", announcement.value)
        }
        PunishmentType.WARN -> {
            return config.warn.announcementRevokeMessage
                .replace("{REASON}", punishment.revertReason ?: "No reason provided.")
                .replace("{TARGET}", punishment.targetName)
                .replace("{ISSUER}", punishment.issuerName)
                .replace("{BCST_MOD}", announcement.value)
        }
        else -> throw IllegalStateException("Cannot revoke this punishment type.")
    }
}