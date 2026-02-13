package net.minepact.api.command.scripts

import net.minepact.api.command.Command
import net.minepact.api.command.CommandUsage
import net.minepact.api.command.arguments.Argument
import net.minepact.api.command.arguments.ArgumentInputType
import net.minepact.api.command.arguments.ExpectedArgument
import net.minepact.api.server.ServerType
import org.bukkit.command.CommandSender

class ScriptCommandBuilder {
    lateinit var name: String
    var description: String = ""
    var permission: String = ""
    var cooldown: Double = -1.0
    var playerOnly: Boolean = false

    private val arguments = mutableListOf<ExpectedArgument>()
    private lateinit var executor: (CommandSender, Map<String, Argument<*>>) -> Unit

    fun argument(
        name: String,
        type: ArgumentInputType,
        optional: Boolean = false,
        block: ExpectedArgument.() -> Unit = {}
    ) {
        arguments += ExpectedArgument(
            name = name,
            inputType = type,
            optional = optional
        ).apply(block)
    }

    fun execute(block: (CommandSender, Map<String, Argument<*>>) -> Unit) {
        executor = block
    }

    fun build(): Command {
        return object : Command(
            ServerType.GLOBAL,
            name,
            description,
            permission,
            CommandUsage(name, arguments),
            cooldown = cooldown,
            playerOnly = playerOnly
        ) {
            override fun execute(
                sender: CommandSender,
                args: MutableList<Argument<*>>
            ): net.minepact.api.command.Result {
                val map = arguments
                    .mapIndexedNotNull { i, arg ->
                        args.getOrNull(i)?.let { arg.name to it }
                    }.toMap()

                executor(sender, map)
                return net.minepact.api.command.Result.SUCCESS
            }
        }
    }
}
