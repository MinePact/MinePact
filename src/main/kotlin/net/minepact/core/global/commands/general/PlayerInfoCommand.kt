package net.minepact.core.global.commands.general

import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Arguments
import net.minepact.api.command.dsl.Command
import net.minepact.api.messages.helper.msg
import net.minepact.api.misc.formatDate
import net.minepact.api.permissions.Permission
import net.minepact.api.player.Player

class PlayerInfoCommand : Command() {
    init {
        command("player-info") {
            description = "Shows information about a player."
            permission = Permission("")

            executes { sender, _ ->
                sender.sendMessage(msg {
                    +"<green>Your Information:\n"
                    +"<yellow>| <green>Unique ID: <white>${sender.data.uuid}\n"
                    +"<yellow>| <green>Discord ID: <white>${sender.data.discordId.ifEmpty { "Not Synced" }}\n"
                    +"<yellow>| <green>Name: <white>${sender.data.name}\n"
                    +"<yellow>| <green>Nickname: <white>${sender.data.nick}\n"
                    +"<yellow>| <green>Chat Colour: <white>${sender.data.chatColour.toHexString(format = HexFormat.UpperCase).substring(2)}\n"
                    +"<yellow>| <green>First Joined: <white>${formatDate(sender.data.firstJoined)}\n"
                    +"<yellow>| <green>Last Seen: <white>${if (sender.online) "Active Now" else formatDate(sender.data.lastSeen)}\n"
                })
                Result.SUCCESS
            }

            argument(Arguments.PLAYERS_OPTIONAL) { executes { sender, args ->
                val target = args[0].value as Player
                sender.sendMessage(msg {
                    +"<green>${target.data.name}'s Information:\n"
                    +"<yellow>| <green>Unique ID: <white>${sender.data.uuid}\n"
                    +"<yellow>| <green>Discord ID: <white>${sender.data.discordId.ifEmpty { "Not Synced" }}\n"
                    +"<yellow>| <green>Name: <white>${sender.data.name}\n"
                    +"<yellow>| <green>Nickname: <white>${sender.data.nick}\n"
                    +"<yellow>| <green>Chat Colour: <white>${sender.data.chatColour.toHexString(format = HexFormat.UpperCase).substring(2)}\n"
                    +"<yellow>| <green>First Joined: <white>${formatDate(sender.data.firstJoined)}\n"
                    +"<yellow>| <green>Last Seen: <white>${if (sender.online) "Active Now" else formatDate(sender.data.lastSeen)}\n"
                })
                Result.SUCCESS
            } }
        }
    }
}