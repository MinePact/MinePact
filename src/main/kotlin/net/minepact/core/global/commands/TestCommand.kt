package net.minepact.core.global.commands

import net.minepact.api.command.Command
import net.minepact.api.command.CommandUsage
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Argument
import net.minepact.api.messages.MessageBuilder
import net.minepact.api.misc.formatDate
import net.minepact.api.player.PlayerRegistry
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class TestCommand : Command(
    name = "test",
    description = "A testing command for the developers.",
    permission = "minepact.dev.test",
    aliases = mutableListOf(""),
    usage = CommandUsage(
        label = "test",
        arguments = listOf()
    ),
    cooldown = 1.0,
    playerOnly = true
) {
    override fun execute(
        sender: CommandSender,
        args: MutableList<Argument<*>>
    ): Result {
        PlayerRegistry.get((sender as Player).uniqueId).thenAccept { player -> run {
            player.sendMessage(MessageBuilder()
                .append("<green>Player Info for <white>${player.data.name} <grey><i>(Hover)") {
                    this.hoverText(
                        "<dark_red><b>Name: <grey>${player.data.name}",
                        "<dark_red><b>Unique ID: <grey>${player.data.uuid}",
                        "<dark_red><b>Discord ID: <grey>${if (player.data.discordId == "") "Not Synced" else player.data.discordId}",
                        "<dark_red><b>First Played: <grey>${formatDate(player.data.firstJoined)}",
                        "<dark_red><b>Last IP: <grey>${player.data.ipHistory.last()}"
                    )
                }.build())
        } }
        return Result.SUCCESS
    }
}