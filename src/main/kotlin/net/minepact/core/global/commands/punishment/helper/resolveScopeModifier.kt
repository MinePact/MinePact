package net.minepact.core.global.commands.punishment.helper

import net.minepact.Main
import net.minepact.api.punishment.PunishmentModifiers

fun resolveScopeModifier(modifiers: List<PunishmentModifiers>): PunishmentModifiers {
    return when {
        modifiers.contains(PunishmentModifiers.GLOBAL) && modifiers.contains(PunishmentModifiers.LOCAL) ->
            PunishmentModifiers.valueOf(Main.MAIN_CONFIG.default_punishment_scope_modifier)
        modifiers.contains(PunishmentModifiers.GLOBAL) -> PunishmentModifiers.GLOBAL
        modifiers.contains(PunishmentModifiers.LOCAL) -> PunishmentModifiers.LOCAL
        else -> PunishmentModifiers.valueOf(Main.MAIN_CONFIG.default_punishment_scope_modifier)
    }
}