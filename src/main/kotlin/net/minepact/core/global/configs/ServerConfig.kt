package net.minepact.core.global.configs

import net.minepact.api.config.AbstractConfigurationFile
import net.minepact.api.config.ReloadableConfig
import java.util.UUID

class ServerConfig : AbstractConfigurationFile(), ReloadableConfig<ServerConfig> {
    override val fileName: String = "server.yml"
    override fun onReload(old: ServerConfig) {
    }

    var uuid by persisting(UUID(0, 0))
    var name by persisting("unknown")
    var type by persisting("GLOBAL")
    var staging by persisting(false)
}
