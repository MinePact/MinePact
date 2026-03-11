package net.minepact.api.permissions

import net.minepact.api.player.PlayerRegistry
import org.bukkit.entity.Player
import org.bukkit.permissions.PermissibleBase

class NMSPermissible(
    private val player: Player
) : PermissibleBase(player) {

    override fun hasPermission(permission: String): Boolean {
        val mpPlayer = PlayerRegistry.get(player.uniqueId).get()!!
        return PermissionManager.hasPermission(mpPlayer, permission)
    }
}