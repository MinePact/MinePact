package net.minepact.api.item.custom

import net.minepact.api.item.enchantments.Enchantment
import net.minepact.api.item.enchantments.EnchantmentInfo
import net.minepact.api.item.enchantments.ItemEnchantmentInfo
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.Locale

fun ItemStack.getEnchantDataString(enchantName: String): String? {
    val pdc = this.persistentDataContainer
    val key = pdc.keys.firstOrNull { it.key.lowercase(Locale.ROOT)
        .startsWith("enchant_$enchantName/".lowercase(Locale.ROOT)) }
    return key?.let { pdc.get(it, PersistentDataType.STRING) }
}
fun ItemStack.getEnchantInfo(enchantName: String): ItemEnchantmentInfo? {
    val pdc = this.persistentDataContainer
    val key = pdc.keys.firstOrNull { it.key.lowercase(Locale.ROOT)
        .startsWith("enchant_$enchantName/".lowercase(Locale.ROOT)) }
    return key?.let { ItemEnchantmentInfo.parseEnchantKey(it.key) }
}
fun ItemStack.getCustomEnchantments(): List<ItemEnchantmentInfo> =
    persistentDataContainer.keys.mapNotNull { ItemEnchantmentInfo.parseEnchantKey(it.key) }
fun ItemStack.getDataContainer(): List<String> = persistentDataContainer.keys.mapNotNull { it.key }
fun ItemStack.getEnchantmentLevel(info: ItemEnchantmentInfo): Int {
    return itemMeta.persistentDataContainer.get(info.getEnchantmentKey(), PersistentDataType.INTEGER) ?: 0
}

fun ItemStack.hasEnchantment(enchantName: String): Boolean {
    val pdc = this.persistentDataContainer
    return pdc.keys.any { key ->
        val keyStr = key.key
        keyStr.startsWith("enchant_$enchantName/")
    }
}
fun ItemStack.hasEnchantment(enchant: Enchantment): Boolean {
    val pdc = this.persistentDataContainer
    val enchantPrefix = "enchant_${enchant.info().name}/".lowercase(Locale.ROOT)
    return pdc.keys.any { key ->
        key.key.lowercase(Locale.ROOT).startsWith(enchantPrefix)
    }
}
fun ItemStack.hasEnchantment(enchantInfo: EnchantmentInfo): Boolean {
    val pdc = this.persistentDataContainer
    val enchantPrefix = "enchant_${enchantInfo.name}/".lowercase(Locale.ROOT)
    return pdc.keys.any { key ->
        key.key.lowercase(Locale.ROOT).startsWith(enchantPrefix)
    }
}

fun ItemStack.enchant(enchant: Enchantment, level: Int, prestige: Int = 0) {
    val enchantInfo = ItemEnchantmentInfo(
        info = enchant.info(),
        currentLevel = level,
        currentPrestige = prestige
    )
    this.applyEnchantmentPDC(enchantInfo)
    val itemMeta = this.itemMeta
    itemMeta.addEnchantmentLore(
        enchantmentInfo = ItemEnchantmentInfo(
            info = enchant.info(),
            currentLevel = level,
            currentPrestige = prestige
        )
    )
}
fun ItemStack.applyEnchantmentPDC(enchantInfo: ItemEnchantmentInfo) {
    val meta = this.itemMeta ?: return
    val pdc = meta.persistentDataContainer

    pdc.set(
        enchantInfo.getEnchantmentKey(),
        PersistentDataType.STRING,
        ""
    )

    this.itemMeta = meta
}

fun ItemStack.removeCustomEnchantment(enchantment: Enchantment) {
    val meta = this.itemMeta ?: return
    val pdc = meta.persistentDataContainer
    val key = pdc.keys.firstOrNull { it.key.lowercase(Locale.ROOT)
        .startsWith("enchant_${enchantment.info().name}/".lowercase(Locale.ROOT)) }
    key?.let { pdc.remove(it) }
    this.itemMeta = meta

    // remove lore
}