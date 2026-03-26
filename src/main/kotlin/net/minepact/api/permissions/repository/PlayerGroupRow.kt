package net.minepact.api.permissions.repository

import java.time.Instant
import java.util.UUID

data class PlayerGroupRow(
    val uuid: UUID,
    val serverId: String,
    val groupName: String,
    val groupServerId: String,
    val expiresAt: Instant?
)