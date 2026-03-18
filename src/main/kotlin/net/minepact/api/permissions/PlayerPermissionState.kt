package net.minepact.api.permissions

import java.util.UUID

data class PlayerPermissionState(
    val uuid: UUID,
    val serverId: String,
    val groups: MutableList<String>,
    val permissions: MutableSet<Permission>
)