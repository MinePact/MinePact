package net.minepact.api.item.enchantments.events

import net.minepact.api.event.EventContext
import net.minepact.api.event.SimpleEventHandler
import net.minepact.api.item.enchantments.EnchantmentTrigger
import org.bukkit.event.entity.EntityDamageEvent as DamageTakenEvent

class DamageTakenHandler : SimpleEventHandler<DamageTakenEvent>() {
    override fun handle(context: EventContext<DamageTakenEvent>) {
        EnchantmentTrigger.DAMAGE_TAKEN.dispatch(context.event)
    }
}