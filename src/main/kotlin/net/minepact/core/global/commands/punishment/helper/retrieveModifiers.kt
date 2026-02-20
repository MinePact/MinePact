package net.minepact.core.global.commands.punishment.helper

import net.minepact.api.punishment.modifier.PunishmentModifier

fun retrieveModifiers(rawTokens: MutableList<String>): MutableList<PunishmentModifier> {
    val modifiers: MutableList<PunishmentModifier> = mutableListOf()
    val tokenIterator = rawTokens.listIterator()
    while (tokenIterator.hasNext()) {
        val token = tokenIterator.next()
        val found = PunishmentModifier.entries.firstOrNull { mod -> mod.possibleIdentifiers.contains(token) }
        if (found != null) {
            modifiers.add(found)
            tokenIterator.remove()
        }
    }
    return modifiers
}