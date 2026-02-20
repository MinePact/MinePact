package net.minepact.api.punishment.modifier

enum class ScopeModifier(
    override val value: String,
    override val possibleIdentifiers: List<String>,
    override val type: ModifierType = ModifierType.PUNISHMENT_SCOPE,
) : PunishmentModifier {
    GLOBAL("Global", listOf("-g", "--g", "-global", "--global")),
    LOCAL("Local", listOf("-l", "--l", "-local", "--local"));
}