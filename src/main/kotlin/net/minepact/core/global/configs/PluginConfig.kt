package net.minepact.core.global.configs

import net.minepact.api.config.AbstractConfigurationFile
import net.minepact.api.config.Comment
import net.minepact.api.config.ReloadableConfig
import net.minepact.api.punishment.modifier.AnnouncementModifier
import net.minepact.api.punishment.modifier.PunishmentModifier
import net.minepact.api.punishment.modifier.ScopeModifier

class PluginConfig : AbstractConfigurationFile(), ReloadableConfig<PluginConfig> {
    override val fileName = "config.yml"
    override fun onReload(old: PluginConfig) {
        /* TODO: Database reload logic */
    }

    var default_announcement_status_modifier: String = AnnouncementModifier.PUBLIC.name
    var default_punishment_scope_modifier: String = ScopeModifier.LOCAL.name

    var webhookUrl: String = ""

    var jumpBoostPlayers: List<String> = emptyList()
    var jumpBoostVelocity by persisting(2.5)

    var database: Database = Database()
    var spawn by persisting(Spawn())

    class Spawn {
        var teleportOnJoin: Boolean = false
        var world: String = "world"
        var x: Double = 0.0
        var y: Double = 64.0
        var z: Double = 0.0
        var yaw: Float = 0.0f
        var pitch: Float = 0.0f
    }
    class Database {
        var provider: String = "MARIADB"
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