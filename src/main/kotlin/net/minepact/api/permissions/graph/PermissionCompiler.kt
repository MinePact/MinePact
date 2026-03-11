package net.minepact.api.permissions.graph

import net.minepact.api.player.Player
import net.minepact.api.permissions.Permission
import java.time.Instant

object PermissionCompiler {
    fun compile(player: Player): CompiledPermissionMap {
        val compiled = CompiledPermissionMap()
        player.permissionData.perms.forEach { if (!expired(it)) compiled.permissions[it.node] = it.value }
        player.groupData.groups.forEach { group ->
            val node = PermissionGraph.getNode(group) ?: return@forEach
            collect(node, compiled.permissions)
        }

        return compiled
    }

    private fun collect(
        node: GroupNode,
        map: MutableMap<String, Boolean>,
        visited: MutableSet<String> = HashSet()
    ) {
        if (!visited.add(node.group.name)) return

        node.permissions.forEach { if (!expired(it)) map[it.node] = it.value }
        node.parents.forEach { collect(it, map, visited) }
    }
    private fun expired(permission: Permission): Boolean {
        return permission.expiresAt != null && Instant.now().isAfter(permission.expiresAt)
    }
}