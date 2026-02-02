package net.minepact.server.global

import net.minepact.api.command.Command
import net.minepact.api.command.CommandUsage
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Argument
import net.minepact.api.command.arguments.ArgumentInputType
import net.minepact.api.command.arguments.ExpectedArgument
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

class PunishCommand : Command(
    name = "punish",
    description = "Punishment command",
    usage = CommandUsage(
        label = "punish",
        arguments = listOf(
            ExpectedArgument(
                potentialValues = listOf("player"),
                optional = false
            ),
            ExpectedArgument(
                potentialValues = listOf("warn", "mute", "ban"),
                optional = false
            ),
            ExpectedArgument(
                potentialValues = listOf("length"),
                inputType = ArgumentInputType.INTEGER,
                optional = true
            )
        )
    ),
    permission = "minepact.command.punish",
    aliases = mutableListOf(),
    cooldown = -1.0,
    playerOnly = false,
    maxArgs = 5
) {
    override fun execute(
        sender: CommandSender,
        args: MutableList<Argument<*>>
    ): Result {

        val targetName = args[0].value as String
        val action = args[1].value as String
        val length: Int = args[2].value as Int

        val target = Bukkit.getPlayerExact(targetName)
        if (target == null) {
            sender.sendMessage("<red>Player not found.")
            return Result.FAILURE
        }

        when (action.lowercase()) {
            "warn" -> {
                target.sendMessage("<yellow>You have been warned: $length")
                sender.sendMessage("<green>$targetName has been warned.")
            }

            "mute" -> {
                sender.sendMessage("<green>$targetName has been muted.")
            }

            "ban" -> {
                target.kickPlayer("You have been banned: $length")
                sender.sendMessage("<green>$targetName has been banned.")
            }

            else -> {
                sender.sendMessage("<red>Unknown punishment type.")
                return Result.FAILURE
            }
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
                        Bukkit.getOnlinePlayers().map { it.name }
                    },
                )
            )
            1 -> mutableListOf(
                ExpectedArgument(
                    potentialValues = listOf("warn", "mute", "ban"),
                    inputType = ArgumentInputType.STRING
                )
            )
            2 -> mutableListOf(
                ExpectedArgument(
                    potentialValues = listOf(),
                    inputType = ArgumentInputType.STRING,
                    optional = true
                )
            )
            else -> mutableListOf()
        }
    }
}