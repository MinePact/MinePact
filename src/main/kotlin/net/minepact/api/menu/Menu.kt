package net.minepact.api.menu

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.entity.Player

open class Menu(
    val id: String,
    val title: Component,
    size: Int,
    private val items: Map<Int, MenuItem>
) : InventoryHolder {
    override fun getInventory(): Inventory = inventory

    private val inventory: Inventory = Bukkit.createInventory(this, size, title)

    init {
        rebuildInventory()
    }

    fun open(player: Player) {
        player.openInventory(inventory)
        MenuManager.registerOpenMenu(player.uniqueId, this)
    }
    fun close(player: Player) {
        player.closeInventory()
        MenuManager.unregisterOpenMenu(player.uniqueId)
    }

    fun rebuildInventory() {
        inventory.clear()
        items.forEach { (slot, menuItem) ->
            val itemStack = menuItem.item.toItemStack()
            val meta = itemStack.itemMeta
            menuItem.displayName?.let { meta.displayName(it) }
            menuItem.lore?.let { meta.lore(it) }
            menuItem.flags.forEach { meta.addItemFlags(it) }
            itemStack.itemMeta = meta
            inventory.setItem(slot, itemStack)
        }
    }
    fun handleClick(player: Player, event: org.bukkit.event.inventory.InventoryClickEvent, slot: Int) {
        val menuItem = items[slot] ?: return
        val ctx = MenuClickContext(player, inventory, event, slot)
        menuItem.onClick?.invoke(ctx)
    }
}