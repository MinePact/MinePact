package net.minepact.core.global.commands.general

import net.minepact.Main
import net.minepact.api.command.Result
import net.minepact.api.command.dsl.Command
import net.minepact.api.permissions.Permission

class ServerInfoCommand : Command() {
    init {
        command("server-info") {
            description = "Shows information about the server."
            permission = Permission("")

            executes { sender, _ ->
                sender.sendMessage("<green>Server Information:")
                sender.sendMessage("<yellow>| <green>Unique ID: <white>${Main.SERVER.info.uuid}")
                sender.sendMessage("<yellow>| <green>Name: <white>${Main.SERVER.info.name}")
                sender.sendMessage("<yellow>| <green>Type: <white>${Main.SERVER.info.type}")
                sender.sendMessage("<yellow>| <green>Version: <white>${Main.instance.server.version}")
                sender.sendMessage("<yellow>| <green>Whitelisted: <white>${Main.SERVER.maintenanceMode()}")
                sender.sendMessage("")
                Result.SUCCESS
            }
        }
    }
}