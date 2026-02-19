package net.minepact.core.global.commands.punishment.helper

import net.minepact.Main
import net.minepact.api.punishment.PunishmentModifiers

fun resolveAnnouncementModifier(modifiers: List<PunishmentModifiers>): PunishmentModifiers {
    return when {
        modifiers.contains(PunishmentModifiers.SILENT) && modifiers.contains(PunishmentModifiers.PUBLIC) ->
            PunishmentModifiers.valueOf(Main.MAIN_CONFIG.default_announcement_status_modifier)
        modifiers.contains(PunishmentModifiers.SILENT) -> PunishmentModifiers.SILENT
        modifiers.contains(PunishmentModifiers.PUBLIC) -> PunishmentModifiers.PUBLIC
        else -> PunishmentModifiers.valueOf(Main.MAIN_CONFIG.default_announcement_status_modifier)
    }
}