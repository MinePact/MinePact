package net.minepact.api.item

import net.kyori.adventure.text.Component
import net.minepact.api.item.enchantments.Enchantment
import net.minepact.api.menu.getItemName
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

open class Item(
    open val material: Material,
    open var name: Component = material.getItemName(),
    open var lore: List<Component> = listOf(),
    val enchantable: Boolean = false,
    val enchantments: MutableMap<Enchantment, Int> = mutableMapOf(),
    val flags: List<ItemFlag> = listOf(),
) {
    fun hasLore(): Boolean {
        return lore.isNotEmpty()
    }
    fun hasFlag(flag: ItemFlag): Boolean {
        return flags.contains(flag)
    }

    fun hasEnchantment(enchantment: Enchantment): Boolean {
        return enchantments.containsKey(enchantment)
    }
    fun addEnchantment(enchantment: Enchantment, level: Int) {
        enchantments[enchantment] = level
    }
    fun removeEnchantment(enchantment: Enchantment) {
        enchantments.remove(enchantment)
    }

    fun toItemStack(): ItemStack {
        val stack = ItemStack(this.material)
        val meta: ItemMeta = stack.itemMeta ?: return stack

        meta.displayName(this.name)

        if (this.hasLore()) {
            meta.lore(this.lore)
        }

        this.flags.forEach { meta.addItemFlags(it) }
        stack.itemMeta = meta
        return stack
    }
}