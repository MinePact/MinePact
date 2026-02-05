package net.minepact.api.event

import net.minepact.Main
import kotlin.collections.mutableListOf

class EventRegister {
    private val HANDLERS: MutableMap<Class<*>, MutableList<EventHandler<*>>> = mutableMapOf()
    val INSTANCES: MutableMap<Class<out EventHandler<*>>, EventHandler<*>> = mutableMapOf()

    fun register(handler: EventHandler<*>) {
        val instance = INSTANCES.getOrPut(handler::class.java) { handler }
        val eventClass = (handler as? SimpleEventHandler<*>)
            ?.eventClass
            ?: throw IllegalArgumentException("Handler must extend SimpleEventHandler!")

        HANDLERS.computeIfAbsent(eventClass) { mutableListOf() }.apply {
            if (!contains(instance)) add(instance)
            sortByDescending { it.priority }
        }
        Main.instance.logger.info("[EventRegister] Registered ${handler.javaClass.name}!")
    }

    @Suppress("UNCHECKED_CAST")
    fun <E : Any> call(event: E): Boolean {
        val context = EventContext(event)
        HANDLERS[event::class.java]
            ?.forEach { handler ->
                (handler as EventHandler<E>).handle(context)
            }
        return context.cancelled
    }
}