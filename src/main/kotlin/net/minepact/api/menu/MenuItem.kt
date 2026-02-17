package net.minepact.api.menu

import net.minepact.api.item.Item
import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemFlag

class MenuItem(
    val item: Item,
    val displayName: Component? = null,
    val lore: List<Component>? = null,
    val flags: List<ItemFlag> = listOf(),
    val onClick: (MenuClickContext.() -> Unit)? = null
)

