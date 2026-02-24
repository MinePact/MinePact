package net.minepact.api.server

import net.minepact.Main
import net.minepact.api.config.ConfigurationRegistry
import net.minepact.api.data.repository.ServerRepository
import net.minepact.core.global.configs.ServerConfig
import org.bukkit.Bukkit
import java.util.UUID

class Server(
    val info: ServerInfo = ServerInfo(
        uuid = ConfigurationRegistry.get(ServerConfig::class).uuid,
        name = ConfigurationRegistry.get(ServerConfig::class).name,
        type = ServerType.valueOf(ConfigurationRegistry.get(ServerConfig::class).type),
        staging = ConfigurationRegistry.get(ServerConfig::class).staging
    ),
    var tps: Double = Bukkit.getServer().tps.last(),
) {
    val CONFIG: ServerConfig = ConfigurationRegistry.get(ServerConfig::class)

    init {
        Main.instance.logger.info("")
        Main.instance.logger.info("[Server] Initializing server configuration...")

        if (CONFIG.uuid == UUID(0, 0) && CONFIG.type == "GLOBAL") {
            CONFIG.uuid = UUID.randomUUID()
            CONFIG.type = "HUB"
        }

        info.uuid = CONFIG.uuid
        info.name = CONFIG.name
        info.type = ServerType.valueOf(CONFIG.type.uppercase())
        info.staging = CONFIG.staging

        Main.instance.logger.info("[Server] UUID: ${info.uuid}")
        Main.instance.logger.info("[Server] Name: ${info.name}")
        Main.instance.logger.info("[Server] Type: ${info.type}")
        Main.instance.logger.info("[Server] Staging: ${info.staging}")

        ServerRepository.insert(info).thenAccept { Main.instance.logger.info("[Server] Updated database!") }
        Main.instance.logger.info("")
    }

    fun updateServerInfo() {
        tps = Bukkit.getServer().tps.last()
    }

    fun maintenanceMode(): Boolean {
        return Bukkit.getServer().hasWhitelist()
    }
}