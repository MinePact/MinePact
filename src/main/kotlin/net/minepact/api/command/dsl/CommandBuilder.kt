package net.minepact.api.command.dsl

import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Argument
import net.minepact.api.command.arguments.ArgumentInputType
import net.minepact.api.command.arguments.ArgumentRange
import net.minepact.api.command.arguments.EmptyArgumentRange
import net.minepact.api.command.arguments.ExpectedArgument
import net.minepact.api.permissions.Permission
import net.minepact.api.player.Player
import org.bukkit.command.CommandSender

@CommandDsl
class CommandBuilder(private val name: String) {
    var description: String = ""
    var permission: Permission? = null
    var playerOnly: Boolean = false
    var cooldown: Double = -1.0
    var log: Boolean = false
    var aliases: MutableList<String> = mutableListOf()

    private val children: MutableList<CommandNode> = mutableListOf()
    private var executor: ((Player, MutableList<Argument<*>>) -> Result)? = null

    fun argument(
        name: String,
        inputType: ArgumentInputType = ArgumentInputType.STRING,
        optional: Boolean = false,
        permission: Permission = Permission(""),
        potentialValues: List<String>? = null,
        range: ArgumentRange<*> = EmptyArgumentRange(),
        senderFilter: ((CommandSender) -> Boolean)? = null,
        dynamicProvider: ((CommandSender) -> List<String>)? = null,
        consumeRemaining: Boolean = false,
        block: CommandBuilder.() -> Unit = {}
    ) {
        val expected = ExpectedArgument(
            name = name,
            potentialValues = potentialValues,
            inputType = inputType,
            optional = optional,
            range = range,
            permission = permission.node,
            senderFilter = senderFilter,
            dynamicProvider = dynamicProvider,
            consumeRemaining = consumeRemaining
        )
        val child = CommandBuilder(name).apply(block)
        children.add(
            CommandNode(
                name = name,
                type = CommandNode.Type.ARGUMENT,
                argument = expected,
                children = child.children.toList(),
                executor = child.executor
            )
        )
    }
    fun argument(
        expected: ExpectedArgument,
        block: CommandBuilder.() -> Unit = {}
    ) {
        val child = CommandBuilder(name).apply(block)
        children.add(
            CommandNode(
                name = name,
                type = CommandNode.Type.ARGUMENT,
                argument = expected,
                children = child.children.toList(),
                executor = child.executor
            )
        )
    }

    fun subcommand(
        name: String,
        description: String = "",
        permission: Permission? = null,
        playerOnly: Boolean = false,
        log: Boolean = false,
        aliases: List<String> = emptyList(),
        block: CommandBuilder.() -> Unit
    ) {
        val builder = CommandBuilder(name).also { b ->
            b.description = description
            permission?.let { b.permission = it }
            b.playerOnly = playerOnly
            b.log = log
            b.aliases = aliases.toMutableList()
            b.block()
        }
        children.add(builder.build())
    }

    fun executes(block: (Player, MutableList<Argument<*>>) -> Result) {
        executor = block
    }

    internal fun build(): CommandNode = CommandNode(
        name = name,
        type = CommandNode.Type.LITERAL,
        description = description,
        permission = permission?.takeIf { it.node.isNotEmpty() },
        playerOnly = playerOnly,
        cooldown = cooldown,
        log = log,
        aliases = aliases.toList(),
        children = children.toList(),
        executor = executor
    )
}