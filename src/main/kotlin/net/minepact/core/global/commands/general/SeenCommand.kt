package net.minepact.core.global.commands.general

import net.minepact.api.command.Provider
import net.minepact.api.command.Result
import net.minepact.api.player.Player
import net.minepact.api.permissions.Permission
import org.bukkit.GameMode
import net.minepact.api.command.arguments.ArgumentInputType
import net.minepact.api.command.dsl.Command
import net.minepact.api.misc.formatDuration

class SeenCommand : Command() {
    init {
        command("seen") {
            description = "Checks when the player was last active."
            permission = Permission("minepact.seen")
            playerOnly = true

            argument(
                name = "player",
                inputType = ArgumentInputType.PLAYER,
                dynamicProvider = Provider.PLAYERS
            ) { executes { player, args ->
                val target = try {
                    args[0].value as Player
                } catch(e: Exception) {
                    player.sendMessage("<red>Could not find <white>${args[0].value as String} <red>in the database.")
                    return@executes Result.SUCCESS
                }

                if (player.online) player.sendMessage("<white>${target.data.name} <green>is currently online.")
                else player.sendMessage("<white>$${target.data.name} <green>was last seen <white>${formatDuration(System.currentTimeMillis() - player.data.lastSeen)} <green>ago.")

                Result.SUCCESS
            } }
        }
    }
}