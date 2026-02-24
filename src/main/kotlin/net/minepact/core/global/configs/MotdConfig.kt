package net.minepact.core.global.configs

import net.minepact.api.config.AbstractConfigurationFile
import net.minepact.api.config.Comment
import net.minepact.api.config.ReloadableConfig
import net.minepact.api.punishment.modifier.AnnouncementModifier
import net.minepact.api.punishment.modifier.PunishmentModifier
import net.minepact.api.punishment.modifier.ScopeModifier
import kotlin.collections.listOf

class MotdConfig : AbstractConfigurationFile(), ReloadableConfig<MotdConfig> {
    override val fileName = "motd.yml"
    override fun onReload(old: MotdConfig) {
    }

    var motd by persisting("<green><bold>Welcome to Minepact!</bold></green><br><yellow>Join our discord for news and updates: <white><bold>discord.gg/minepact",)
    var maintenance by persisting("<dark_red><bold>Server is currently under maintenance!</bold></dark_red><br><red>Try again later!</red>")
}