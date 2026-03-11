package net.minepact.api.permissions

import java.util.UUID

data class PlayerPermissionState(
    val uuid: UUID,
    val groups: MutableList<String>,
    val permissions: MutableSet<Permission>
)