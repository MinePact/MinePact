package net.minepact.api.item.enchantments

import org.bukkit.event.Event
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

enum class EnchantmentTrigger(
    val eventClass: Class<out Event>
) {
    MOB_HIT(EntityDamageByEntityEvent::class.java),
    DAMAGE_TAKEN(EntityDamageEvent::class.java),
    BLOCK_BREAK(BlockBreakEvent::class.java);

    val handlers = mutableMapOf<Class<out Event>, MutableList<(Event) -> Unit>>()

    fun <T : Event> addHandler(type: Class<T>, handler: (T) -> Unit) {
        val list = handlers.getOrPut(type) { mutableListOf() }
        @Suppress("UNCHECKED_CAST")
        list += handler as (Event) -> Unit
    }

    fun dispatch(event: Event) {
        handlers[event::class.java]?.forEach { it(event) }
    }
}