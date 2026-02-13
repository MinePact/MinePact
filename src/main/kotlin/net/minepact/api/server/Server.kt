package net.minepact.api.server

import net.minepact.Main
import net.minepact.api.config.ConfigurationRegistry
import net.minepact.core.global.configs.ServerConfig
import java.util.UUID

class Server {
    val CONFIG: ServerConfig = ConfigurationRegistry.get(ServerConfig::class)

    var uuid: UUID
    var name: String
    var type: ServerType
    var staging: Boolean

    init {
        Main.instance.logger.info("")
        Main.instance.logger.info("[Server] Initializing server configuration...")

        Main.instance.logger.info("")
        Main.instance.logger.info("[Server] UUID: ${CONFIG.uuid}")
        Main.instance.logger.info("[Server] Name: ${CONFIG.name}")
        Main.instance.logger.info("[Server] Type: ${CONFIG.type}")
        Main.instance.logger.info("[Server] Staging: ${CONFIG.staging}")
        Main.instance.logger.info("")

        if (CONFIG.uuid == UUID(0, 0) && CONFIG.type == "GLOBAL") {
            CONFIG.uuid = UUID.randomUUID()
            CONFIG.type = "HUB"
        }

        uuid = CONFIG.uuid
        name = CONFIG.name
        type = ServerType.valueOf(CONFIG.type.uppercase())
        staging = CONFIG.staging

        Main.instance.logger.info("[Server] UUID: $uuid")
        Main.instance.logger.info("[Server] Name: $name")
        Main.instance.logger.info("[Server] Type: $type")
        Main.instance.logger.info("[Server] Staging: $staging")
        Main.instance.logger.info("")
    }
}