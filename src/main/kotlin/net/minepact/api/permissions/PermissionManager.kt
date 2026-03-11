package net.minepact.api.permissions

import net.minepact.api.permissions.graph.PermissionCompiler
import net.minepact.api.player.Player

object PermissionManager {
    fun hasPermission(player: Player, node: String): Boolean {
        var compiled = PermissionCache.get(player.data.uuid)

        if (compiled == null) {
            compiled = PermissionCompiler.compile(player)
            PermissionCache.put(player.data.uuid, compiled)
        }

        return compiled.has(node)
    }

}