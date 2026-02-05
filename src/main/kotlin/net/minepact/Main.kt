package net.minepact

import net.minepact.api.command.CommandRegister
import net.minepact.api.event.BukkitEventBridge
import net.minepact.api.event.EventRegister
import net.minepact.api.reflections.findCommands
import net.minepact.api.reflections.findEvents
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    companion object {
        lateinit var instance: Main
        lateinit var COMMAND_REGISTRY: CommandRegister
        lateinit var EVENT_REGISTRY: EventRegister
    }

    override fun onLoad() {
        instance = this
        COMMAND_REGISTRY = CommandRegister()
        EVENT_REGISTRY = EventRegister()
    }

    override fun onEnable() {
        findCommands(this, "net.minepact.server").forEach { COMMAND_REGISTRY.register(it) }
        findEvents(this, "net.minepact.server").forEach { EVENT_REGISTRY.register(it) }

        BukkitEventBridge(EVENT_REGISTRY).registerAllEvents()
    }

    override fun onDisable() {

    }
}
