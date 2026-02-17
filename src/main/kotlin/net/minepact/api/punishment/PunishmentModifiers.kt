package net.minepact.api.punishment

enum class PunishmentModifiers(
    val type: ModifierType,
    val possibleIdentifiers: List<String>
) {
    SILENT(
        ModifierType.ANNOUNCEMENT_STATUS,
        listOf("-s", "--s", "-silent", "--silent")
    ),
    PUBLIC(
        ModifierType.ANNOUNCEMENT_STATUS,
        listOf("-p", "--p", "-public", "--public")
    ),
    GLOBAL(
        ModifierType.PUNISHMENT_SCOPE,
        listOf("-g", "--g", "-global", "--global")
    ),
    LOCAL(
        ModifierType.PUNISHMENT_SCOPE,
        listOf("-l", "--l", "-local", "--local")
    );

    enum class ModifierType {
        ANNOUNCEMENT_STATUS,
        PUNISHMENT_SCOPE
    }
}