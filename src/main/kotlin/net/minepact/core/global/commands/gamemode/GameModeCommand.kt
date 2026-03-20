package net.minepact.core.global.commands.gamemode

import net.minepact.api.command.CommandUsage
import net.minepact.api.command.Provider
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Argument
import net.minepact.api.command.arguments.ExpectedArgument
import net.minepact.api.messages.send
import net.minepact.api.player.Player
import net.minepact.api.permissions.Permission
import net.minepact.api.server.ServerType
import org.bukkit.Bukkit
import org.bukkit.GameMode
import net.minepact.api.command.Command

class GameModeCommand : Command(
    server = ServerType.SURVIVAL,
    name = "gamemode",
    description = "Changes the player's gamemode.",
    usage = CommandUsage(
        label = "gamemode",
        arguments = listOf(
            ExpectedArgument(name = "gamemode", dynamicProvider = Provider.GAMEMODES),
            ExpectedArgument(name = "player", dynamicProvider = Provider.PLAYERS, optional = true),
        )
    ),
    permission = Permission("minepact.gamemode"),
    aliases = mutableListOf("gm"),
    cooldown = 5.0,
    playerOnly = true
) {
    override fun execute(
        sender: Player, args: MutableList<Argument<*>>
    ): Result {
        val gameMode = args[0].value as String
        val targetName = if (args.size > 1) args[1].value as String else sender.data.name

        try {
            val target = Bukkit.getPlayerExact(targetName)!!
            target.gameMode = GameMode.valueOf(gameMode.uppercase())
            sender.sendMessage(
                if (target == sender) "<green>Your gamemode has been set to $gameMode."
                else "<green>Set $targetName's gamemode to $gameMode."
            )
        }catch (e: NullPointerException){
            sender.sendMessage("<red>Player not found.")
            return Result.FAILURE
        }
        return Result.SUCCESS
    }
}