package net.minepact.core.global.commands.staff.punishment.helper.message

import net.minepact.api.config.experimental.ConfigurationRegistry
import net.minepact.api.player.Player
import net.minepact.api.player.PlayerRegistry
import net.minepact.api.punishment.Punishment
import net.minepact.api.punishment.PunishmentType
import net.minepact.api.punishment.modifier.AnnouncementModifier
import net.minepact.core.global.configs.PunishmentConfig

fun getRevokalMessage(punishment: Punishment, announcement: AnnouncementModifier): String {
    val config: PunishmentConfig = ConfigurationRegistry.get(PunishmentConfig::class)

    val issuer: Player = PlayerRegistry.get(punishment.issuer).get()
    val target: Player = PlayerRegistry.get(punishment.target).get()
    
    when (punishment.type) {
        PunishmentType.IP_BAN -> {
            return config.ipBan.announcementRevokeMessage
                .replace("{REASON}", punishment.revertReason ?: "No reason provided.")
                .replace("{TARGET}", target.data.name)
                .replace("{ISSUER}", issuer.data.name)
                .replace("{BCST_MOD}", announcement.value)
        }
        PunishmentType.BAN -> {
            return config.ban.announcementRevokeMessage
                .replace("{REASON}", punishment.revertReason ?: "No reason provided.")
                .replace("{TARGET}", target.data.name)
                .replace("{ISSUER}", issuer.data.name)
                .replace("{BCST_MOD}", announcement.value)
        }
        PunishmentType.MUTE -> {
            return config.mute.announcementRevokeMessage
                .replace("{REASON}", punishment.revertReason ?: "No reason provided.")
                .replace("{TARGET}", target.data.name)
                .replace("{ISSUER}", issuer.data.name)
                .replace("{BCST_MOD}", announcement.value)
        }
        PunishmentType.WARN -> {
            return config.warn.announcementRevokeMessage
                .replace("{REASON}", punishment.revertReason ?: "No reason provided.")
                .replace("{TARGET}", target.data.name)
                .replace("{ISSUER}", issuer.data.name)
                .replace("{BCST_MOD}", announcement.value)
        }
        else -> throw IllegalStateException("Cannot revoke this punishment type.")
    }
}