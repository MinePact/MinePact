package net.minepact.api.command

import net.minepact.api.config.ConfigurationRegistry
import net.minepact.api.punishment.modifier.AnnouncementModifier
import net.minepact.api.punishment.modifier.ScopeModifier
import net.minepact.api.server.ServerType
import org.bukkit.GameMode
import org.bukkit.command.CommandSender

/**
 * A provider is a function that takes in a CommandSender and returns a list of potential values for an argument.
 *
 * @see net.minepact.api.command.arguments.ExpectedArgument
 *
 * @author dankenyon - 22/02/26
 */
class Provider {
    companion object {
        val EMPTY: (CommandSender) -> List<String> = { _ -> emptyList() }

        val PLAYERS: (CommandSender) -> List<String> = { sender -> sender.server.onlinePlayers.map { it.name } }
        val WORLDS: (CommandSender) -> List<String> = { sender -> sender.server.worlds.map { it.name } }
        val SERVERS: (CommandSender) -> List<String> = { _ -> ServerType.entries.map { it.name.lowercase() } }
        val GAMEMODES: (CommandSender) -> List<String> = { sender -> GameMode.entries.map { it.name.lowercase() }.filter { mode ->
            sender.hasPermission("minepact.gamemode.$mode")
        } }

        val PUNISHMENT_MODIFIERS: (CommandSender) -> List<String> = { _ -> ScopeModifier.entries.map { it.possibleIdentifiers.map { v -> v } }.flatten() + AnnouncementModifier.entries.map { it.possibleIdentifiers.map { v -> v } }.flatten() }
        val CONFIG_ACTIONS: (CommandSender) -> List<String> = { _ -> listOf("get", "set", "reload") }
        val CONFIG_NAMES: (CommandSender) -> List<String> = { _ -> ConfigurationRegistry.configs.keys.map { it.simpleName!! } }
    }
}
