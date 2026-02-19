package net.minepact.api.punishment

class ModifierPair(
    announcementModifier: PunishmentModifiers,
    scopeModifier: PunishmentModifiers
) {
    init {
        if (announcementModifier.type != PunishmentModifiers.ModifierType.ANNOUNCEMENT_STATUS) {
            throw IllegalArgumentException("Announcement modifier must be of type ANNOUNCEMENT_STATUS")
        }
        if (scopeModifier.type != PunishmentModifiers.ModifierType.PUNISHMENT_SCOPE) {
            throw IllegalArgumentException("Scope modifier must be of type PUNISHMENT_SCOPE")
        }
    }
}