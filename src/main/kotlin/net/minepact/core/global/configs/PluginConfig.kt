package net.minepact.core.global.configs

import net.minepact.api.config.AbstractConfigurationFile
import net.minepact.api.config.Comment
import net.minepact.api.config.ReloadableConfig

class PluginConfig : AbstractConfigurationFile(), ReloadableConfig<PluginConfig> {
    override val fileName = "config.yml"
    override fun onReload(old: PluginConfig) {
        /* TODO: Database reload logic */
    }

    @Comment("The URL used for server updates.")
    var webhookUrl: String? = null

    @Comment("Jump Boost -> Crouch Jump Launch")
    var jumpBoostPlayers: List<String> = emptyList()
    var jumpBoostVelocity: Double = 2.5

    @Comment("Database Connection Information")
    var database: Database = Database()

    class Database {
        @Comment("The ip to connect to")
        var host: String = "localhost"
        @Comment("The port to connect through")
        var port: Int = 3306
        @Comment("The name of the database")
        var name: String = "minepact"
        @Comment("The username of the user connecting")
        var username: String = "root"
        @Comment("The password of the user connecting")
        var password: String = "password"
    }
}