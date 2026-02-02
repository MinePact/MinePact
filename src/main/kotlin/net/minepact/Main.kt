package net.minepact

import net.minepact.api.command.CommandRegister
import net.minepact.api.command.findCommands
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    companion object {
        lateinit var instance: Main
        lateinit var COMMAND_REGISTRY: CommandRegister
    }

    override fun onLoad() {
        instance = this
        COMMAND_REGISTRY = CommandRegister()
    }

    override fun onEnable() {
        findCommands(this, "net.minepact.server").forEach { COMMAND_REGISTRY.register(it) }
    }

    override fun onDisable() {

    }
}
