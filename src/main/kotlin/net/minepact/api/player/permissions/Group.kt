package net.minepact.api.player.permissions

data class Group(
    val name: String,
    val displayName: String?,
    val weight: Int = 0,
    val prefix: String? = null,
    val suffix: String? = null,
    val permissions: Set<Permission> = emptySet()
)