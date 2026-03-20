package net.minepact.core.global.commands.general

import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Arguments
import net.minepact.api.command.dsl.Command
import net.minepact.api.misc.formatDate
import net.minepact.api.permissions.Permission

class PlayerInfoCommand : Command() {
    init {
        command("player-info") {
            description = "Shows information about a player."
            permission = Permission("")

            executes { sender, _ ->
                sender.sendMessage("<green>Your Information:")
                sender.sendMessage("<yellow>| <green>Unique ID: <white>${sender.data.uuid}")
                sender.sendMessage("<yellow>| <green>Discord ID: <white>${sender.data.discordId.ifEmpty { "Not Synced" }}")
                sender.sendMessage("<yellow>| <green>Name: <white>${sender.data.name}")
                sender.sendMessage("<yellow>| <green>Nickname: <white>${sender.data.nick}")
                sender.sendMessage("<yellow>| <green>Chat Colour: <white>${sender.data.chatColour.toHexString(format = HexFormat.UpperCase).substring(2)}")
                sender.sendMessage("<yellow>| <green>First Joined: <white>${formatDate(sender.data.firstJoined)}")
                sender.sendMessage("<yellow>| <green>Last Seen: <white>${if (sender.online) "Active Now" else formatDate(sender.data.lastSeen)}")
                sender.sendMessage("")
                Result.SUCCESS
            }

            argument(Arguments.PLAYERS_OPTIONAL) { executes { sender, args ->
                val target = args[0].value as net.minepact.api.player.Player
                sender.sendMessage("<green>${target.data.name}'s Information:")
                sender.sendMessage("<yellow>| <green>Unique ID: <white>${target.data.uuid}")
                sender.sendMessage("<yellow>| <green>Discord ID: <white>${target.data.discordId.ifEmpty { "Not Synced" }}")
                sender.sendMessage("<yellow>| <green>Name: <white>${target.data.name}")
                sender.sendMessage("<yellow>| <green>Nickname: <white>${target.data.nick}")
                sender.sendMessage("<yellow>| <green>Chat Colour: <white>${target.data.chatColour.toHexString(format = HexFormat.UpperCase).substring(2)}")
                sender.sendMessage("<yellow>| <green>First Joined: <white>${formatDate(target.data.firstJoined)}")
                sender.sendMessage("<yellow>| <green>Last Seen: <white>${if (target.online) "Active Now" else formatDate(target.data.lastSeen)}")
                sender.sendMessage("")
                Result.SUCCESS
            } }
        }
    }
}