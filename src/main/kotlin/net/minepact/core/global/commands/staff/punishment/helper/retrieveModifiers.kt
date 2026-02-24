package net.minepact.core.global.commands.staff.punishment.helper

import net.minepact.api.punishment.modifier.AnnouncementModifier
import net.minepact.api.punishment.modifier.PunishmentModifier
import net.minepact.api.punishment.modifier.ScopeModifier

fun retrieveModifiers(rawTokens: MutableList<String>): MutableList<PunishmentModifier> {
    val modifiers: MutableList<PunishmentModifier> = mutableListOf()
    val tokenIterator = rawTokens.listIterator()
    while (tokenIterator.hasNext()) {
        val token = tokenIterator.next()
        val foundScope = ScopeModifier.entries.firstOrNull { mod -> mod.possibleIdentifiers.contains(token) }
        val foundAnnouncement = AnnouncementModifier.entries.firstOrNull { mod -> mod.possibleIdentifiers.contains(token) }

        if (foundScope != null) {
            modifiers.add(foundScope)
            tokenIterator.remove()
        }
        if (foundAnnouncement != null) {
            modifiers.add(foundAnnouncement)
            tokenIterator.remove()
        }
    }
    return modifiers
}