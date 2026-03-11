package net.minepact.api.permissions

import net.minepact.api.constants.PERMISSION_SEPARATOR

data class PlayerPermissionData(
    val perms: MutableSet<Permission>,
) {
    override fun toString(): String = perms.joinToString(separator = "$PERMISSION_SEPARATOR")
}