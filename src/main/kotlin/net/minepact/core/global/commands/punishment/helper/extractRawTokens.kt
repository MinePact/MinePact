package net.minepact.core.global.commands.punishment.helper

import net.minepact.api.command.arguments.Argument

fun extractRawTokens(args: MutableList<Argument<*>>): MutableList<String> {
    return if (args.size > 1) args.drop(1).map { it.value as String }.toMutableList()
    else mutableListOf()
}