package net.minepact.api.menu

import net.kyori.adventure.text.Component
import net.minepact.api.item.Item
import org.bukkit.inventory.ItemFlag

class MenuItemBuilder(private val item: Item) {
    private var displayName: Component? = null
    private var lore: List<Component>? = null
    private var flags: List<ItemFlag> = listOf()
    private var onClick: (MenuClickContext.() -> Unit)? = null

    fun displayName(name: Component): MenuItemBuilder { this.displayName = name; return this }
    fun lore(l: List<Component>): MenuItemBuilder { this.lore = l; return this }
    fun flags(f: List<ItemFlag>): MenuItemBuilder { this.flags = f; return this }
    fun onClick(handler: MenuClickContext.() -> Unit): MenuItemBuilder { this.onClick = handler; return this }

    fun build(): MenuItem = MenuItem(item, displayName, lore, flags, onClick)
}