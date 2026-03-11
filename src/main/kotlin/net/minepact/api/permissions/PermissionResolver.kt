package net.minepact.api.permissions

import net.minepact.api.player.Player
import java.time.Instant

object PermissionResolver {
    fun resolve(player: Player, node: String): Boolean {
        val permissions = mutableMapOf<String, Boolean>()

        player.permissionData.perms.forEach { if (!expired(it)) permissions[it.node] = it.value }
        player.groupData.groups.forEach { group -> collectGroupPermissions(group, permissions) }
        permissions[node]?.let { return it }

        val parts = node.split(".")
        for (i in parts.indices.reversed()) {
            val wildcard = parts.take(i).joinToString(".") + ".*"
            permissions[wildcard]?.let { return it }
        }

        return false
    }

    private fun collectGroupPermissions(group: Group, permissions: MutableMap<String, Boolean>) {
        if (group.isExpired()) return

        group.permissions.forEach { if (!expired(it)) permissions[it.node] = it.value }
        group.parents.forEach {
            val parent = GroupRegistry.get(it).get() ?: return@forEach
            collectGroupPermissions(parent, permissions)
        }
    }
    private fun expired(permission: Permission): Boolean = permission.expiresAt != null && Instant.now().isAfter(permission.expiresAt)
}