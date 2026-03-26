package net.minepact.api.permissions.repository

import java.time.Instant

data class GroupPermissionRow(
    val groupName: String,
    val groupServerId: String,
    val node: String,
    val value: Boolean,
    val expiresAt: Instant?
)