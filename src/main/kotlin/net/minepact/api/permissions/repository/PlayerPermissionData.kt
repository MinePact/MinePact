package net.minepact.api.permissions.repository

import net.minepact.api.constants.PERMISSION_SEPARATOR
import net.minepact.api.permissions.Permission

data class PlayerPermissionData(
    val perms: MutableSet<Permission>,
) {
    override fun toString(): String = perms.joinToString(separator = "$PERMISSION_SEPARATOR")
}