package net.minepact.core.global.enchantments

import net.minepact.api.item.enchantments.*
import net.minepact.api.player.PlayerRegistry
import org.bukkit.event.block.BlockBreakEvent

class VeinMinerEnchant : EnchantmentAction(
    info = EnchantmentInfo(
        name = "Vein Miner",
        colour = "green",
        maxLevel = 5,
        maxPrestige = 3,
        startPercentage = 50.0, // 0.1%
        finalPercentage = 50.0, // 5%
        applicable = listOf(EnchantmentType.Single.PICKAXE),
        trigger = EnchantmentTrigger.BLOCK_BREAK,
        triggerChanceType = TriggerChanceType.STATIC,
        exponentialScale = 4
    )
) {
    init {
        on<BlockBreakEvent> { event, level ->
            PlayerRegistry.get(event.player.uniqueId).thenAccept { player -> run {
                    player.sendMessage("hi")
                }
            }
        }
    }
}