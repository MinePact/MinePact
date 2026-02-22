package net.minepact.core.global.commands.punishment.helper

import net.minepact.api.misc.getLengthFromIdentifier

fun parseLength(tokens: MutableList<String>): Pair<String, Long> {
    val lengthIndex = tokens.indexOfFirst { it.equals("permanent", true) || it.matches(Regex("^\\d+[smhdwMy]$")) }

    val lengthToken = if (lengthIndex >= 0) tokens.removeAt(lengthIndex) else "permanent"
    val expiresAt = if (lengthToken.equals("permanent", true)) {
        Long.MIN_VALUE
    } else {
        try {
            System.currentTimeMillis() + getLengthFromIdentifier(lengthToken) + 1_000
        } catch (_: Exception) {
            Long.MIN_VALUE
        }
    }

    return lengthToken to expiresAt
}