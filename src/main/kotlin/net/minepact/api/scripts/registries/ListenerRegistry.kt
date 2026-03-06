package net.minepact.api.scripts.registries

import org.bukkit.event.HandlerList
import org.bukkit.event.Listener

class ListenerRegistry {
    private val listeners = mutableMapOf<String, MutableList<Listener>>()

    fun track(scriptName: String, listener: Listener) {
        listeners.getOrPut(scriptName) { mutableListOf() }.add(listener)
    }

    fun unregisterScript(scriptName: String) {
        listeners.remove(scriptName)?.forEach { HandlerList.unregisterAll(it) }
    }
    fun unregisterAll() {
        listeners.values.flatten().forEach { HandlerList.unregisterAll(it) }
        listeners.clear()
    }
}