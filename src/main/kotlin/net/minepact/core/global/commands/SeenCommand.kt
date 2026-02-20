package net.minepact.core.global.commands

import net.minepact.api.command.Command
import net.minepact.api.command.CommandUsage
import net.minepact.api.command.Provider
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Argument
import net.minepact.api.command.arguments.ExpectedArgument
import net.minepact.api.messages.send
import net.minepact.api.misc.formatDuration
import net.minepact.api.player.PlayerRegistry
import org.bukkit.command.CommandSender

class SeenCommand : Command(
    name = "seen",
    description = "Finds when a player last joined the server.",
    permission = "minepact.seen",
    usage = CommandUsage(label = "seen", arguments = listOf(
        ExpectedArgument(name = "player", dynamicProvider = Provider.PLAYERS)
    ))
) {
    override fun execute(
        sender: CommandSender,
        args: MutableList<Argument<*>>
    ): Result {
        val targetName: String = args[0].value as String

        PlayerRegistry.get(targetName).thenAccept { player ->
            if (player != null) {
                if (player.online) sender.send("<white>$targetName <green>is currently online.")
                else sender.send("<white>$targetName <green>was last seen <white>${formatDuration(System.currentTimeMillis() - player.data.lastSeen)} <green>ago.")
            } else sender.send("<red>Could not find <white>$targetName <red>in the database.")
        }
        return Result.SUCCESS
    }
}