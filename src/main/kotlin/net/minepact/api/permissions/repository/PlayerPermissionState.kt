package net.minepact.api.permissions.repository

import net.minepact.api.permissions.Permission

import java.util.UUID

data class PlayerPermissionState(
    val uuid: UUID,
    val serverId: String,
    val groups: MutableList<String>,
    val permissions: MutableSet<Permission>
)