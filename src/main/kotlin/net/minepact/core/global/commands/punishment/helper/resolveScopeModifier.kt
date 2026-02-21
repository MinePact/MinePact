package net.minepact.core.global.commands.punishment.helper

import net.minepact.Main
import net.minepact.api.punishment.modifier.AnnouncementModifier
import net.minepact.api.punishment.modifier.PunishmentModifier
import net.minepact.api.punishment.modifier.ScopeModifier

fun resolveScopeModifier(modifiers: List<PunishmentModifier>): ScopeModifier {
    val scopeModifiers = modifiers.filterIsInstance<ScopeModifier>()

    return when {
        scopeModifiers.contains(ScopeModifier.GLOBAL) && scopeModifiers.contains(ScopeModifier.LOCAL) ->
            ScopeModifier.valueOf(Main.MAIN_CONFIG.default_punishment_scope_modifier)
        scopeModifiers.contains(ScopeModifier.GLOBAL) -> ScopeModifier.GLOBAL
        scopeModifiers.contains(ScopeModifier.LOCAL) -> ScopeModifier.LOCAL
        else -> ScopeModifier.valueOf(Main.MAIN_CONFIG.default_punishment_scope_modifier)
    }
}