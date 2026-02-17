package net.minepact.api.menu

import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory

class MenuClickContext(
    val player: Player,
    val inventory: Inventory,
    val event: InventoryClickEvent,
    val slot: Int
) {
    fun close() = player.closeInventory()
}

