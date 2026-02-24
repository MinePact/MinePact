package net.minepact.core.global.commands.staff.punishment.helper

fun parseReason(tokens: MutableList<String>): String {
    return if (tokens.isNotEmpty()) tokens.joinToString(" ")
    else "No reason provided."
}