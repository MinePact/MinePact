package net.minepact.api.punishment.modifier

enum class AnnouncementModifier(
    override val value: String,
    override val possibleIdentifiers: List<String>,
    override val type: ModifierType = ModifierType.ANNOUNCEMENT_STATUS,
) : PunishmentModifier {
    SILENT("Silent", listOf("-s", "--s", "-silent", "--silent")),
    PUBLIC("Public", listOf("-p", "--p", "-public", "--public"));
}