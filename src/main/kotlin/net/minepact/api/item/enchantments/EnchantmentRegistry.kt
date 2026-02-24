package net.minepact.api.item.enchantments

object EnchantmentRegistry {
    private val enchants = mutableListOf<Enchantment>()

    fun register(enchant: Enchantment) {
        enchants += enchant
    }

    fun all(): List<Enchantment> = enchants
    fun getByName(name: String): Enchantment? = enchants.firstOrNull { it.action().info.name == name }
}