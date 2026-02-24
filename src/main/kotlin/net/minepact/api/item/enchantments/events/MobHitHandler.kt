package net.minepact.api.item.enchantments.events

import net.minepact.api.event.EventContext
import net.minepact.api.event.SimpleEventHandler
import net.minepact.api.item.enchantments.EnchantmentTrigger
import org.bukkit.event.entity.EntityDamageByEntityEvent as MobHitEvent

class MobHitHandler :SimpleEventHandler<MobHitEvent>() {
    override fun handle(context: EventContext<MobHitEvent>) {
        EnchantmentTrigger.MOB_HIT.dispatch(context.event)
    }
}