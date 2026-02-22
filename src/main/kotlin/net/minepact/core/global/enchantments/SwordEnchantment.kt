package net.minepact.core.global.enchantments

import net.minepact.api.item.enchantments.Enchantment
import net.minepact.api.item.enchantments.EnchantmentAction

enum class SwordEnchantment(
    var action: EnchantmentAction,
    var description: String
) : Enchantment {
    VEIN_MINER(
        action = VeinMinerEnchant(),
        description = "Increases the experience gained from defeating enemies."
    );

    override fun action(): EnchantmentAction {
        return action
    }
    override fun description(): String {
        return description
    }
}