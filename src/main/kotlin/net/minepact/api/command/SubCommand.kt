package net.minepact.api.command

import net.minepact.api.command.arguments.Argument
import net.minepact.api.command.arguments.ExpectedArgument
import org.bukkit.command.CommandSender

@Deprecated("")
abstract class SubCommand(
    val name: String,
    val permission: String? = null
) {
    abstract val usage: List<ExpectedArgument>
    abstract fun execute(sender: CommandSender, args: List<Argument<*>>): Result
}