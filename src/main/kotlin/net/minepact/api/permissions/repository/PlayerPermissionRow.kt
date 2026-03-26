package net.minepact.api.permissions.repository

import java.time.Instant
import java.util.UUID

data class PlayerPermissionRow(
    val uuid: UUID,
    val serverId: String,
    val node: String,
    val value: Boolean,
    val expiresAt: Instant?
)