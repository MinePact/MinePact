package net.minepact.api.command.dsl

import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Argument
import net.minepact.api.command.arguments.ExpectedArgument
import net.minepact.api.permissions.Permission
import net.minepact.api.player.Player

class CommandNode(
    val name: String,
    val type: Type,
    val description: String = "",
    val permission: Permission? = null,
    val playerOnly: Boolean = false,
    val cooldown: Double = -1.0,
    val log: Boolean = false,
    val aliases: List<String> = emptyList(),
    val argument: ExpectedArgument? = null,
    val children: List<CommandNode> = emptyList(),
    val executor: ((Player, MutableList<Argument<*>>) -> Result)? = null
) {
    enum class Type {
        LITERAL,
        ARGUMENT
    }

    fun matches(input: String): Boolean = when (type) {
        Type.LITERAL -> name.equals(input, ignoreCase = true) || aliases.any { it.equals(input, ignoreCase = true) }
        Type.ARGUMENT -> true
    }
}