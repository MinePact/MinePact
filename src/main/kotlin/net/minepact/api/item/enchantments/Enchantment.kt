package net.minepact.api.item.enchantments

interface Enchantment {
    fun action(): EnchantmentAction
    fun description(): String

    fun info(): EnchantmentInfo {
        return action().info
    }
    fun getCustomName(): String {
        return "<${info().colour}><bold>${info().name}<reset>"
    }
}