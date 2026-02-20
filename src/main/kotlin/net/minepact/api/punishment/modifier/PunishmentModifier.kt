package net.minepact.api.punishment.modifier

interface PunishmentModifier {
    val value: String
    val type: ModifierType
    val possibleIdentifiers: List<String>
}