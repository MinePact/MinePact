package net.minepact.core.global.commands.gamemode

import net.minepact.api.command.Command
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

class CreativeCommand : Command(
    name = "creative",
    aliases = mutableListOf("gmc", "gmcreative", "gamemodecreative"),
    description = "Creates a player's game as a game for creative.",
    permission = Permission("minepact.gamemode.creative"),
    playerOnly = true,
    usage = CommandUsage(
        label = "creative", arguments = listOf(
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
            target.gameMode = GameMode.CREATIVE

            if (sender.asPlayer() == target) sender.sendMessage("<green>You are now in <yellow>creative <green>mode.")
            else {
                sender.sendMessage("<green>${target.name} is now in <yellow>creative <green>mode.")
                target.sendMessage("<green>You are now in <yellow>creative <green>mode.")
            }
            return Result.SUCCESS
        }catch (e: NullPointerException) {
            sender.sendMessage("<red>Player not found.")
            return Result.FAILURE
        }
    }
}