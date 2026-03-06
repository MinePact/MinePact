package net.minepact.core.global.configs

import net.minepact.api.config.experimental.AbstractConfigurationFile
import net.minepact.api.config.experimental.ReloadableConfig

class MotdConfig : AbstractConfigurationFile(), ReloadableConfig<MotdConfig> {
    override val fileName = "motd.yml"
    override fun onReload(old: MotdConfig) {
    }

    var motd by persisting("<green><bold>Welcome to Minepact!</bold></green><br><yellow>Join our discord for news and updates: <white><bold>discord.gg/minepact",)
    var maintenance by persisting("<dark_red><bold>Server is currently under maintenance!</bold></dark_red><br><red>Try again later!</red>")
}