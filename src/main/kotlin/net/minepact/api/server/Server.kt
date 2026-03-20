package net.minepact.api.server

import net.minepact.Main
import net.minepact.api.config.custom.ConfigManager
import net.minepact.api.config.custom.helper.MinePactConfigType
import net.minepact.api.config.custom.helper.get
import net.minepact.api.config.custom.interfaces.FileReader
import net.minepact.api.config.custom.interfaces.FileWriter
import net.minepact.api.data.repository.ServerRepository
import org.bukkit.Bukkit
import java.util.UUID

class Server(
    val info: ServerInfo = ServerInfo(
        uuid = UUID.fromString(ConfigManager.file<MinePactConfigType>("server.mpc").reader.get<String>("uuid")),
        name = ConfigManager.file<MinePactConfigType>("server.mpc").reader.get<String>("name"),
        type = ServerType.valueOf(ConfigManager.file<MinePactConfigType>("server.mpc").reader.get<String>("type")),
        staging = ConfigManager.file<MinePactConfigType>("server.mpc").reader.get<Boolean>("staging")
    )
) {
    init {
        Main.instance.logger.info("")
        Main.instance.logger.info("[Server] Initializing server configuration...")

        val r: FileReader = ConfigManager.file<MinePactConfigType>("server.mpc").reader
        val w: FileWriter = ConfigManager.file<MinePactConfigType>("server.mpc").writer

        if (r.get<String>("uuid") == UUID(0, 0).toString() && r.get<String>("type") == "GLOBAL") {
            w.set("uuid", UUID.randomUUID().toString())
            w.set("type", "HUB")

            w.save()
        }

        info.uuid = UUID.fromString(r.get<String>("uuid"))
        info.name = r.get<String>("name")
        info.type = ServerType.valueOf(r.get<String>("type").uppercase())
        info.staging = r.get<Boolean>("staging")

        Main.instance.logger.info("[Server] UUID: ${info.uuid}")
        Main.instance.logger.info("[Server] Name: ${info.name}")
        Main.instance.logger.info("[Server] Type: ${info.type}")
        Main.instance.logger.info("[Server] Staging: ${info.staging}")

        ServerRepository.insert(info).thenAccept { Main.instance.logger.info("[Server] Updated database!") }
        Main.instance.logger.info("")
    }

    fun maintenanceMode(): Boolean {
        return Bukkit.getServer().hasWhitelist()
    }
}