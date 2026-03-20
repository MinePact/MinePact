package net.minepact.core.global.commands.gamemode

import net.minepact.api.command.CommandUsage
import net.minepact.api.command.Provider
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Argument
import net.minepact.api.command.arguments.ExpectedArgument
import net.minepact.api.messages.send
import net.minepact.api.player.Player
import net.minepact.api.permissions.Permission
import org.bukkit.Bukkit
import org.bukkit.GameMode
import net.minepact.api.command.Command

class SpectatorCommand : Command(
    name = "spectator",
    aliases = mutableListOf("gmsp", "gmspectator", "gamemodespectator"),
    description = "Creates a player's game as a game for spectator.",
    permission = Permission("minepact.gamemode.spectator"),
    playerOnly = true,
    usage = CommandUsage(
        label = "spectator", arguments = listOf(
            ExpectedArgument(name = "player", dynamicProvider = Provider.PLAYERS, optional = true)
        )
    ),
) {
    override fun execute(
        sender: Player,
        args: MutableList<Argument<*>>
    ): Result {
        try {
            val targetName = if (args.isNotEmpty()) args[0].value as String else sender.data.name
            val target = Bukkit.getPlayerExact(targetName)!!
            target.gameMode = GameMode.SPECTATOR

            if (sender.asPlayer() == target) sender.sendMessage("<green>You are now in <yellow>spectator <green>mode.")
            else {
                sender.sendMessage("<green>${target.name} is now in <yellow>spectator <green>mode.")
                target.sendMessage("<green>You are now in <yellow>spectator <green>mode.")
            }
            return Result.SUCCESS
        }catch (e: NullPointerException) {
            sender.sendMessage("<red>Player not found.")
            return Result.FAILURE
        }
    }
}