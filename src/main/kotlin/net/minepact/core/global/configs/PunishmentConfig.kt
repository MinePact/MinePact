package net.minepact.core.global.configs

import net.minepact.api.config.experimental.AbstractConfigurationFile
import net.minepact.api.config.experimental.Comment
import net.minepact.api.config.experimental.ReloadableConfig

class PunishmentConfig : AbstractConfigurationFile(), ReloadableConfig<PunishmentConfig> {
    override val fileName: String = "punishments.yml"
    override fun onReload(old: PunishmentConfig) {
        // TODO
    }

    @Comment("{ISSUER} {TARGET} {REASON} {EXPIRES_IN} {EXPIRES_AT} {BCST_MOD} {SCOPE_MOD}")
    var ipBan: IpBan = IpBan()
    var ban: Ban = Ban()
    var mute: Mute = Mute()
    var warn: Warn = Warn()
    var kick: Kick = Kick()

    class IpBan {
        var message: List<String> = listOf(
            "<dark_red>You cannot join this server!</dark_red>",
            "<red>You are ip-banned!</red>",
            "",
            "<red>Banned By: <white>{ISSUER}</white></red>",
            "<red>Reason: <white>{REASON}</white></red>",
            "<red>Expires: <white>{EXPIRES_AT}</white></red>",
            "<red>Remaining Length: <white>{EXPIRES_IN}</white></red>"
        )
        var announcementMessage: String = "<white>{TARGET} <green>was ip-banned by <white>{ISSUER} <green>for <white>{REASON}<green> <grey><i>[{BCST_MOD}]</i></grey>"
        var announcementRevokeMessage: String = "<white>{TARGET} <green>has been un ip-banned <white>{ISSUER} <green>for <white>{REASON}<green> <grey><i>[{BCST_MOD}]</i></grey>"
    }
    class Ban {
        var message: List<String> = listOf(
            "<dark_red>You cannot join this server!</dark_red>",
            "<red>You are banned!</red>",
            "",
            "<red>Banned By: <white>{ISSUER}</white></red>",
            "<red>Reason: <white>{REASON}</white></red>",
            "<red>Expires: <white>{EXPIRES_AT}</white></red>",
            "<red>Remaining Length: <white>{EXPIRES_IN}</white></red>"
        )
        var announcementMessage: String = "<white>{TARGET} <green>was banned by <white>{ISSUER} <green>for <white>{REASON}<green> <grey><i>[{BCST_MOD}]</i></grey>"
        var announcementRevokeMessage: String = "<white>{TARGET} <green>has been unbanned <white>{ISSUER} <green>for <white>{REASON}<green> <grey><i>[{BCST_MOD}]</i></grey>"
    }
    class Mute {
        var message: List<String> = listOf(
            "<dark_red>You have been muted!</dark_red> <red>You will be unmuted in <white>{EXPIRES_IN}</white>.</red>",
            "<red>Reason: <white>{REASON}"
        )
        var announcementMessage: String = "<white>{TARGET} <green>was muted by <white>{ISSUER} <green>for <white>{REASON}<green> <grey><i>[{BCST_MOD}]</i></grey>"
        var announcementRevokeMessage: String = "<white>{TARGET} <green>has been unmuted by <white>{ISSUER} <green>for <white>{REASON}<green> <grey><i>[{BCST_MOD}]</i></grey>"

        var chatAttemptMessage: List<String> = listOf("<dark_red>You are muted!</dark_red> <red>You will be unmuted in <white>{EXPIRES_IN}</white>.</red>")
    }
    class Warn {
        var message: List<String> = listOf(
            "<dark_red>You have been warned!</dark_red> <red>Your warn will expire in <white>{EXPIRES_IN}</white>.</red>",
            "<red>Reason: <white>{REASON}"
        )
        var announcementMessage: String = "<white>{TARGET} <green>was warned by <white>{ISSUER} <green>for <white>{REASON}<green> <grey><i>[{BCST_MOD}]</i></grey>"
        var announcementRevokeMessage: String = "<white>{TARGET} <green>has been unwarned by <white>{ISSUER} <green>for <white>{REASON}<green> <grey><i>[{BCST_MOD}]</i></grey>"
    }
    class Kick {
        var message: List<String> = listOf(
            "<dark_red>You have been kicked!</dark_red>",
            "",
            "<red>Kicked By: <white>{ISSUER}</white></red>",
            "<red>Reason: <white>{REASON}</white></red>",
        )
        var announcementMessage: String = "<white>{TARGET} <green>was kicked by <white>{ISSUER} <green>for <white>{REASON}<green> <grey><i>[{BCST_MOD}]</i></grey>"
    }
}