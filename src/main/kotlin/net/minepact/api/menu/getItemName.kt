package net.minepact.api.menu

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material

fun Material.getItemName(): Component {
    val name: String = this.name.lowercase().replace("_", " ")
    return MiniMessage.miniMessage().deserialize(name[0].uppercase() + name.substring(1))
}