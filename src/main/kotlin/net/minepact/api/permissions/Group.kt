package net.minepact.api.permissions

import java.time.Instant

data class Group(
    val name: String,
    val serverId: String,
    var displayName: String? = null,
    var weight: Int = 0,
    var prefix: String? = null,
    var suffix: String? = null,
    val permissions: MutableSet<Permission> = mutableSetOf(),
    val parents: MutableSet<String> = mutableSetOf(),
    val expiresAt: Instant? = null
) {
    fun isTemporary(): Boolean = expiresAt != null
    fun isExpired(): Boolean = expiresAt != null && Instant.now().isAfter(expiresAt)
}