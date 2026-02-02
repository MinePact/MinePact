package net.minepact.api.command

import net.minepact.api.command.arguments.Argument
import net.minepact.api.command.arguments.ExpectedArgument
import org.bukkit.command.CommandSender

abstract class Command(
        val name: String,
        val permission: String,
        val description: String,
        val usage: CommandUsage,
        val aliases: MutableList<String>,
        var cooldown: Double = -1.0,
        val playerOnly: Boolean = false,
        val maxArgs: Int = Int.MAX_VALUE
) {
    open val subCommands: Map<String, SubCommand> = emptyMap()

    abstract fun execute(sender: CommandSender, args: MutableList<Argument<*>>): Result
    abstract fun chatComplete(index: Int): MutableList<ExpectedArgument>
}
