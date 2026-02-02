package net.minepact.api.command

import net.minepact.api.command.arguments.ExpectedArgument

class CommandUsage(
    val label: String,
    val arguments: List<ExpectedArgument>
) {
    override fun toString(): String {
        return "/$label " + arguments.joinToString(" ") {
            if (it.optional) "[${it.potentialValues.joinToString("|")}]"
            else "<${it.potentialValues.joinToString("|")}>"
        }
    }
}