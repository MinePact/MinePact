package net.minepact.api.item.enchantments

import net.minepact.api.item.custom.getCustomEnchantments
import net.minepact.api.item.custom.hasEnchantment
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import kotlin.math.pow

abstract class EnchantmentAction(
    var info: EnchantmentInfo
) {
    fun calculateTriggerChance(level: Int): Double {
        return when (info.triggerChanceType) {
            TriggerChanceType.LINEAR -> calculateLinear(level)
            TriggerChanceType.EXPONENTIAL -> calculateExponential(level)
            TriggerChanceType.STATIC -> info.startPercentage
        }
    }
    private fun calculateLinear(level: Int): Double {
        val start = info.startPercentage
        val end = info.finalPercentage
        val max = info.maxLevel

        if (level <= 1) return start
        if (level >= max) return end

        val progress = (level - 1).toDouble() / (max - 1)
        return start + (end - start) * progress
    }
    private fun calculateExponential(level: Int): Double {
        val start = info.startPercentage
        val end = info.finalPercentage
        val max = info.maxLevel

        if (max <= 1) return end

        val normalized = level.toDouble() / max.toDouble()
        val scale = info.exponentialScale.coerceIn(1, 10)
        val exponent = 1.0 + (scale / 10.0)
        val curveValue = normalized.pow(exponent)

        return start + (end - start) * curveValue
    }

    protected inline fun <reified T : Event> on(noinline handler: (T, Int) -> Unit) {
        info.trigger.addHandler(T::class.java) { event ->
            val player = when (event) {
                is EntityDamageByEntityEvent -> event.damager as? Player
                is EntityDamageEvent -> event.entity as? Player
                is BlockBreakEvent -> event.player
                else -> null
            } ?: return@addHandler
            if (!player.inventory.itemInMainHand.hasEnchantment(this@EnchantmentAction.info)) {
                return@addHandler
            }

            val itemEnchantments: List<ItemEnchantmentInfo> = player.inventory.itemInMainHand.getCustomEnchantments()
            val enchantmentName: String = this@EnchantmentAction.info.name
            val enchantmentLevel: Int = itemEnchantments.find {
                it.info.name.equals(enchantmentName, ignoreCase = true)
            }?.currentLevel ?: return@addHandler

            val result = act(enchantmentLevel)
            if (result == EnchantmentResult.SUCCESS) handler(event, enchantmentLevel)
        }
    }

    fun act(level: Int): EnchantmentResult {
        val chance = calculateTriggerChance(level)
        val roll = Math.random() * 100.0
        return if (roll <= chance) EnchantmentResult.SUCCESS else EnchantmentResult.FAILURE
    }
}