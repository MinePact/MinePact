package net.minepact.core.global.commands.gamemode

import net.minepact.api.command.Command
import net.minepact.api.command.CommandUsage
import net.minepact.api.command.Provider
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Argument
import net.minepact.api.command.arguments.ExpectedArgument
import net.minepact.api.messages.send
import net.minepact.api.server.ServerType
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.command.CommandSender

class GameModeCommand : Command(
    server = ServerType.SURVIVAL,
    name = "gamemode",
    description = "Changes the player's gamemode.",
    usage = CommandUsage(
        label = "gamemode",
        arguments = listOf(
            ExpectedArgument(name = "gamemode", dynamicProvider = Provider.GAMEMODES,),
            ExpectedArgument(name = "player", dynamicProvider = Provider.PLAYERS, optional = true),
        )
    ),
    permission = "minepact.command.gamemode",
    aliases = mutableListOf("gm"),
    cooldown = 5.0,
    playerOnly = true
) {
    override fun execute(
        sender: CommandSender, args: MutableList<Argument<*>>
    ): Result {
        val gameMode = args[0].value as String
        val targetName = if (args.size > 1) args[1].value as String else sender.name

        try {
            val target = Bukkit.getPlayerExact(targetName)!!
            target.gameMode = GameMode.valueOf(gameMode.uppercase())
            sender.send(
                if (target == sender) "<green>Your gamemode has been set to $gameMode."
                else "<green>Set $targetName's gamemode to $gameMode."
            )
        }catch (e: NullPointerException){
            sender.send("<red>Player not found.")
            return Result.FAILURE
        }
        return Result.SUCCESS
    }
}