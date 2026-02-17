package net.minepact.api.item.custom

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minepact.api.item.enchantments.ItemEnchantmentInfo
import org.bukkit.inventory.meta.ItemMeta

fun ItemMeta.setLore(lore: List<String>) {
    val l: MutableList<Component> = mutableListOf()
    lore.map { l += MiniMessage.miniMessage().deserialize(it) }
    this.lore(l)
}

fun ItemMeta.removeLoreItalics() {
    val currentLore = this.lore() ?: return
    val newLore = currentLore.map { it.decoration(TextDecoration.ITALIC, false) }
    this.lore(newLore)
}

fun ItemMeta.addEnchantmentLore(enchantmentInfo: ItemEnchantmentInfo) {
    setEnchantmentsLore(listOf(enchantmentInfo))
}

fun ItemMeta.setEnchantmentsLore(enchantments: List<ItemEnchantmentInfo>) {
    val loreLines: MutableList<Component> = mutableListOf()

    loreLines += MiniMessage.miniMessage().deserialize(" ")
    loreLines += MiniMessage.miniMessage().deserialize("<green><bold>Enchantments")

    if (enchantments.isEmpty()) {
        loreLines += MiniMessage.miniMessage().deserialize("<grey><bold>| <reset><red>None")
    } else {
        enchantments.forEach { info ->
            val colour = info.info.colour
            val name = info.info.name
            val level = info.currentLevel
            loreLines += MiniMessage.miniMessage().deserialize("<grey><bold>| <${colour}><bold>${name}<reset> <white>${level}")
        }
    }

    this.lore(loreLines)
}