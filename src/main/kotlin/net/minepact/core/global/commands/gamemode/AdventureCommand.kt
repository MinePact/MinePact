package net.minepact.core.global.commands.gamemode

import net.minepact.api.command.Command
import net.minepact.api.command.CommandUsage
import net.minepact.api.command.Provider
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Argument
import net.minepact.api.command.arguments.ExpectedArgument
import net.minepact.api.messages.send
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.command.CommandSender

class AdventureCommand : Command(
    name = "adventure",
    aliases = mutableListOf("gma", "gmadventure", "gamemodeadventure"),
    description = "Creates a player's game as a game for adventure.",
    permission = "",
    playerOnly = true,
    usage = CommandUsage(
        label = "adventure", arguments = listOf(
            ExpectedArgument(name = "player", dynamicProvider = Provider.PLAYERS, optional = true)
        )
    ),
) {
    override fun execute(
        sender: CommandSender,
        args: MutableList<Argument<*>>
    ): Result {
        try {
            val targetName = if (args.isNotEmpty()) args[0].value as String else sender.name
            val target = Bukkit.getPlayerExact(targetName)!!
            target.gameMode = GameMode.ADVENTURE
            return Result.SUCCESS
        }catch (e: NullPointerException) {
            sender.send("<red>Player not found.")
            return Result.FAILURE
        }
    }
}