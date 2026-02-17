package net.minepact.core.global.events.enchantments

import net.minepact.api.event.EventContext
import net.minepact.api.event.SimpleEventHandler
import net.minepact.api.item.enchantments.EnchantmentTrigger
import org.bukkit.event.block.BlockBreakEvent as BlockBrokenEvent

class BlockBreakHandler : SimpleEventHandler<BlockBrokenEvent>() {
    override fun handle(context: EventContext<BlockBrokenEvent>) {
        EnchantmentTrigger.BLOCK_BREAK.dispatch(context.event)
    }
}