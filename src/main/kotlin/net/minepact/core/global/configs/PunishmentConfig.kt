package net.minepact.core.global.configs

import net.minepact.api.config.AbstractConfigurationFile
import net.minepact.api.config.Comment
import net.minepact.api.config.ReloadableConfig

class PunishmentConfig : AbstractConfigurationFile(), ReloadableConfig<PunishmentConfig> {
    override val fileName: String = "punishments.yml"
    override fun onReload(old: PunishmentConfig) {
        // TODO
    }

    @Comment("{ISSUER} {TARGET} {REASON} {EXPIRES_IN} {EXPIRES_AT}")
    var ban: Ban = Ban()

    class Ban {
        var kickMessage: List<String> = listOf(
            "<dark_red>You cannot join this server!</dark_red>",
            "<red>You are banned!</red>",
            "",
            "<red>Banned By: <white>{ISSUER}</white></red>",
            "<red>Reason: <white>{REASON}</white></red>",
            "<red>Expires: <white>{EXPIRES}</white></red>",
            "<red>Remaining Length: <white>{EXPIRES_IN}</white></red>"
        )
    }
    class Mute {
    }
    class Warn {

    }
}