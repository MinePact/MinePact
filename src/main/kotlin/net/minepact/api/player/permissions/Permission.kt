package net.minepact.api.player.permissions

import java.time.Instant

data class Permission(
    val node: String,
    val expiresAt: Instant? = null
) {
    fun isTemporary(): Boolean = expiresAt != null
}