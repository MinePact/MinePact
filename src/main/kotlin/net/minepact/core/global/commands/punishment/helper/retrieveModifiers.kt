package net.minepact.core.global.commands.punishment.helper

import net.minepact.api.punishment.PunishmentModifiers

fun retrieveModifiers(rawTokens: MutableList<String>): MutableList<PunishmentModifiers> {
    val modifiers: MutableList<PunishmentModifiers> = mutableListOf()
    val tokenIterator = rawTokens.listIterator()
    while (tokenIterator.hasNext()) {
        val token = tokenIterator.next()
        val found = PunishmentModifiers.entries.firstOrNull { mod -> mod.possibleIdentifiers.contains(token) }
        if (found != null) {
            modifiers.add(found)
            tokenIterator.remove()
        }
    }
    return modifiers
}