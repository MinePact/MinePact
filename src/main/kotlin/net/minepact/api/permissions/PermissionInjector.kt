package net.minepact.api.permissions

import org.bukkit.craftbukkit.entity.CraftHumanEntity
import org.bukkit.permissions.PermissibleBase
import org.bukkit.entity.Player
import java.lang.reflect.Field

object PermissionInjector {
    private val field: Field =
        CraftHumanEntity::class.java
            .getDeclaredField("perm")
            .apply { isAccessible = true }

    fun inject(player: Player) {
        field.set(player as CraftHumanEntity, NMSPermissible(player))
    }
}