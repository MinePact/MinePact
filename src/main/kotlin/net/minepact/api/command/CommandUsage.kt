package net.minepact.api.command

import net.minepact.api.command.arguments.ExpectedArgument

/**
 * Represents the usage of a command, including its label and expected arguments.
 *
 * @property label The base label of the command (e.g., "teleport").
 * @property arguments A list of expected arguments for the command, which may be optional or required.
 *
 * @see ExpectedArgument
 *
 * @author dankenyon - 22/02/26
 */
class CommandUsage(
    val label: String,
    val arguments: List<ExpectedArgument>
) {
    override fun toString(): String {
        return "/$label " + arguments.joinToString(" ") {
            if (it.optional) "[${it.name}]"
            else "<${it.name}>"
        }
    }
}