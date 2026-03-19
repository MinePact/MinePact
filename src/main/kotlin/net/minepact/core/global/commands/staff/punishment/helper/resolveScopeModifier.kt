package net.minepact.core.global.commands.staff.punishment.helper

import net.minepact.Main
import net.minepact.api.config.custom.ConfigManager
import net.minepact.api.config.custom.helper.MinePactConfigType
import net.minepact.api.config.custom.helper.get
import net.minepact.api.punishment.modifier.PunishmentModifier
import net.minepact.api.punishment.modifier.ScopeModifier

fun resolveScopeModifier(modifiers: List<PunishmentModifier>): ScopeModifier {
    val scopeModifiers = modifiers.filterIsInstance<ScopeModifier>()

    return when {
        scopeModifiers.contains(ScopeModifier.GLOBAL) && scopeModifiers.contains(ScopeModifier.LOCAL) ->
            ScopeModifier.valueOf(ConfigManager.file<MinePactConfigType>("config.mpc").reader.get<String>("punishments.default_scope"))
        scopeModifiers.contains(ScopeModifier.GLOBAL) -> ScopeModifier.GLOBAL
        scopeModifiers.contains(ScopeModifier.LOCAL) -> ScopeModifier.LOCAL
        else -> ScopeModifier.valueOf(ConfigManager.file<MinePactConfigType>("config.mpc").reader.get<String>("punishments.default_scope"))
    }
}