package net.minepact

import net.minepact.api.command.CommandRegister
import net.minepact.server.global.PunishCommand
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
        COMMAND_REGISTRY.register(PunishCommand())
    }

    override fun onDisable() {

    }
}
