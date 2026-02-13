package net.minepact.api.command

import net.minepact.api.config.ConfigurationRegistry
import net.minepact.api.server.ServerType
import org.bukkit.GameMode
import org.bukkit.command.CommandSender

class Provider {
    companion object {
        val PLAYERS: (CommandSender) -> List<String> = { sender -> sender.server.onlinePlayers.map { it.name } }
        val WORLDS: (CommandSender) -> List<String> = { sender -> sender.server.worlds.map { it.name } }
        val SERVERS: (CommandSender) -> List<String> = { _ -> ServerType.entries.map { it.name.lowercase() } }
        val GAMEMODES: (CommandSender) -> List<String> = { sender -> GameMode.entries.map { it.name.lowercase() }.filter { mode ->
            sender.hasPermission("minepact.gamemode.$mode")
        } }

        val CONFIG_ACTIONS: (CommandSender) -> List<String> = { _ -> listOf("get", "set", "reload") }
        val CONFIG_NAMES: (CommandSender) -> List<String> = { _ -> ConfigurationRegistry.configs.keys.map { it.simpleName!! } }
    }
}
