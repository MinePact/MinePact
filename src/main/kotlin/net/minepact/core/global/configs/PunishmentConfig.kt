package net.minepact.core.global.configs

import net.minepact.api.config.AbstractConfigurationFile
import net.minepact.api.config.Comment
import net.minepact.api.config.ReloadableConfig

class PunishmentConfig : AbstractConfigurationFile(), ReloadableConfig<PunishmentConfig> {
    override val fileName: String = "punishments.yml"
    override fun onReload(old: PunishmentConfig) {
        // TODO
    }

    @Comment("{ISSUER} {TARGET} {REASON} {EXPIRES_IN} {EXPIRES_AT} {BCST_MOD} {SCOPE_MOD}")
    var ban: Ban = Ban()
    var mute: Mute = Mute()
    var warn: Warn = Warn()

    class Ban {
        var kickMessage: List<String> = listOf(
            "<dark_red>You cannot join this server!</dark_red>",
            "<red>You are banned!</red>",
            "",
            "<red>Banned By: <white>{ISSUER}</white></red>",
            "<red>Reason: <white>{REASON}</white></red>",
            "<red>Expires: <white>{EXPIRES_AT}</white></red>",
            "<red>Remaining Length: <white>{EXPIRES_IN}</white></red>"
        )
        var announcementMessage: String = "<white>{TARGET} <green>was banned by <white>{ISSUER} <green>for <white>{REASON}<green> <grey><i>[{BCST_MOD}]</i></grey>"
    }
    class Mute {
        var muteMessage: List<String> = listOf(
            "<dark_red>You have been muted!</dark_red> <red>You will be unmuted in <white>{EXPIRES_IN}</white>.</red>",
            "<red>Reason: <white>{REASON}"
        )
        var chatAttemptMessage: List<String> = listOf("<dark_red>You are muted!</dark_red> <red>You will be unmuted in <white>{EXPIRES_IN}</white>.</red>")
        var announcementMessage: String = "<white>{TARGET} <green>was muted by <white>{ISSUER} <green>for <white>{REASON}<green> <grey><i>[{BCST_MOD}]</i></grey>"
    }
    class Warn {
        var warnMessage: List<String> = listOf(
            "<dark_red>You have been warned!</dark_red> <red>Your warn will expire in <white>{EXPIRES_IN}</white>.</red>",
            "<red>Reason: <white>{REASON}"
        )
        var announcementMessage: String = "<white>{TARGET} <green>was warned by <white>{ISSUER} <green>for <white>{REASON}<green> <grey><i>[{BCST_MOD}]</i></grey>"
    }
}