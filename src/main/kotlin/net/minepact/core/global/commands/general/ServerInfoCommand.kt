package net.minepact.core.global.commands.general

import net.minepact.Main
import net.minepact.api.command.Result
import net.minepact.api.command.dsl.Command
import net.minepact.api.messages.helper.msg
import net.minepact.api.permissions.Permission

class ServerInfoCommand : Command() {
    init {
        command("server-info") {
            description = "Shows information about the server."
            permission = Permission("")

            executes { sender, _ ->
                sender.sendMessage(msg {
                    +"<green>Server Information:\n"
                    +"<yellow>| <green>Unique ID: <white>${Main.SERVER.info.uuid}\n"
                    +"<yellow>| <green>Name: <white>${Main.SERVER.info.name}\n"
                    +"<yellow>| <green>Type: <white>${Main.SERVER.info.type}\n"
                    +"<yellow>| <green>Version: <white>${Main.instance.server.version}\n"
                    +"<yellow>| <green>Whitelisted: <white>${Main.SERVER.maintenanceMode()}\n"
                    +" "
                })
                Result.SUCCESS
            }
        }
    }
}