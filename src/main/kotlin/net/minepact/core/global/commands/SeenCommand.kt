package net.minepact.core.global.commands

import net.minepact.api.command.CommandUsage
import net.minepact.api.command.Provider
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Argument
import net.minepact.api.command.arguments.ExpectedArgument
import net.minepact.api.messages.send
import net.minepact.api.misc.formatDuration
import net.minepact.api.player.Player
import net.minepact.api.player.PlayerRegistry
import net.minepact.api.permissions.Permission
import net.minepact.api.command.Command

class SeenCommand : Command(
    name = "seen",
    description = "Finds when a player last joined the server.",
    permission = Permission("minepact.seen"),
    usage = CommandUsage(label = "seen", arguments = listOf(
        ExpectedArgument(name = "player", dynamicProvider = Provider.PLAYERS)
    ))
) {
    override fun execute(
        sender: Player,
        args: MutableList<Argument<*>>
    ): Result {
        val targetName: String = args[0].value as String

        PlayerRegistry.get(targetName).thenAccept { player ->
            if (player != null) {
                if (player.online) sender.sendMessage("<white>$targetName <green>is currently online.")
                else sender.sendMessage("<white>$targetName <green>was last seen <white>${formatDuration(System.currentTimeMillis() - player.data.lastSeen)} <green>ago.")
            } else sender.sendMessage("<red>Could not find <white>$targetName <red>in the database.")
        }
        return Result.SUCCESS
    }
}