package net.minepact.core.global.commands

import net.minepact.api.command.Command
import net.minepact.api.command.CommandUsage
import net.minepact.api.command.Provider
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Argument
import net.minepact.api.command.arguments.ArgumentInputType
import net.minepact.api.command.arguments.ExpectedArgument
import net.minepact.api.messages.send
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class OperatorCommand : Command(
    name = "op",
    description = "Make another player an operator!",
    permission = "minepact.admin.op",
    aliases = mutableListOf(""),
    usage = CommandUsage(
        label = "op", arguments = listOf(
            ExpectedArgument(name = "player", dynamicProvider = Provider.PLAYERS, inputType = ArgumentInputType.STRING)
        )
    ),
    cooldown = 1.0,
    log = true
) {
    override fun execute(
        sender: CommandSender,
        args: MutableList<Argument<*>>
    ): Result {
        val playerName = args[0].value as String
        try {
            val player: Player = Bukkit.getPlayer(playerName)!!

            if (player.isOp) {
                sender.send("<red>${player.name} is already an operator!")
                return Result.FAILURE
            }
            player.isOp = true
            sender.send("<green>${player.name} is now an operator!")
            player.send("<green>You are now an operator!")
        }catch (e: Exception){
            val player: OfflinePlayer = Bukkit.getPlayer(playerName)!!

            if (player.isOp) {
                sender.send("<red>${player.name} is already an operator!")
                return Result.FAILURE
            }
            player.isOp = true
            sender.send("<green>${player.name} is now an operator!")
            return Result.FAILURE
        }
        return Result.SUCCESS
    }
}