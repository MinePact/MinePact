package net.minepact.core.global.commands.staff.punishment.helper

import net.minepact.Main
import net.minepact.api.punishment.modifier.AnnouncementModifier
import net.minepact.api.punishment.modifier.PunishmentModifier

fun resolveAnnouncementModifier(modifiers: List<PunishmentModifier>): AnnouncementModifier {
    val announcementModifiers = modifiers.filterIsInstance<AnnouncementModifier>()

    return when {
        announcementModifiers.contains(AnnouncementModifier.SILENT) && announcementModifiers.contains(AnnouncementModifier.PUBLIC) ->
            AnnouncementModifier.valueOf(Main.MAIN_CONFIG.default_announcement_status_modifier)
        announcementModifiers.contains(AnnouncementModifier.SILENT) -> AnnouncementModifier.SILENT
        announcementModifiers.contains(AnnouncementModifier.PUBLIC) -> AnnouncementModifier.PUBLIC
        else -> AnnouncementModifier.valueOf(Main.MAIN_CONFIG.default_announcement_status_modifier)
    }
}