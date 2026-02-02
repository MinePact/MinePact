package net.minepact.server.global

import net.minepact.api.command.Command
import net.minepact.api.command.CommandUsage
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Argument
import net.minepact.api.command.arguments.ArgumentInputType
import net.minepact.api.command.arguments.ExpectedArgument
import net.minepact.api.messages.send
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.command.CommandSender
import java.util.Locale
import java.util.Locale.getDefault

class GameModeCommand : Command(
    name = "gamemode",
    description = "Changes the players gamemode.",
    usage = CommandUsage(
        label = "gamemode",
        arguments = listOf(
            ExpectedArgument(
                potentialValues = listOf("survival", "creative", "adventure", "spectator"),
                optional = false
            ),
            ExpectedArgument(
                potentialValues = listOf("player"),
                optional = true
            ),
        )
    ),
    permission = "minepact.command.gamemode",
    aliases = mutableListOf("gm"),
    cooldown = 1.0,
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

    override fun chatComplete(index: Int): MutableList<ExpectedArgument> {
        return when (index) {
            0 -> mutableListOf(
                ExpectedArgument(
                    potentialValues = listOf(),
                    inputType = ArgumentInputType.STRING,
                    dynamicProvider = {
                        GameMode.entries.map { it.name.lowercase() }
                    },
                )
            )
            1 -> mutableListOf(
                ExpectedArgument(
                    potentialValues = listOf(),
                    inputType = ArgumentInputType.STRING,
                    dynamicProvider = {
                        Bukkit.getOnlinePlayers().map { it.name }
                    },
                )
            )
            else -> mutableListOf()
        }
    }
}