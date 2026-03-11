package net.minepact.api.menu

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minepact.api.item.Item

class MenuBuilder(private val id: String) {
    private var title: String = "Menu"
    private var size: Int = 9
    private val items: MutableMap<Int, MenuItem> = mutableMapOf()

    fun title(title: Component): MenuBuilder { this.title = title.toString(); return this }
    fun size(size: Int): MenuBuilder { this.size = size; return this }

    fun item(slot: Int, item: Item, setup: (MenuItemBuilder.() -> Unit)? = null) : MenuBuilder {
        val builder = MenuItemBuilder(item)
        setup?.invoke(builder)
        items[slot] = builder.build()
        return this
    }

    fun build(): Menu = Menu(id, MiniMessage.miniMessage().deserialize(title), size, items)
}

