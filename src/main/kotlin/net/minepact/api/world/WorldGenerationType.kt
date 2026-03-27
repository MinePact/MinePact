package net.minepact.api.world

/**
 * Supported generation types - we include the common Bukkit `WorldType` names
 * plus `VOID` for an empty/void-like world.
 */
enum class WorldGenerationType {
    NORMAL,
    FLAT,
    LARGE_BIOMES,
    AMPLIFIED,
    VOID
}
