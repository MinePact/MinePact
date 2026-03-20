package net.minepact.core.global.commands.gamemode

import net.minepact.api.command.Provider
import net.minepact.api.command.Result
import net.minepact.api.player.Player
import net.minepact.api.permissions.Permission
import org.bukkit.GameMode
import net.minepact.api.command.arguments.ArgumentInputType
import net.minepact.api.command.dsl.Command

class SurvivalCommand : Command() {
    init {
        val gm = GameMode.ADVENTURE
        command(gm.name.lowercase()) {
            description = "Turns the players gamemode to ${gm.name.lowercase()}."
            permission = Permission("minepact.gamemode.${gm.name.lowercase()}")
            aliases = mutableListOf("gm${gm.name.lowercase()[0]}")
            playerOnly = true

            executes { sender, _ ->
                if (sender.asPlayer()!!.gameMode == gm) {
                    sender.sendMessage("<red>You are already in <yellow>${gm.name.lowercase()} <red>mode.")
                    Result.SUCCESS
                }
                sender.asPlayer()!!.gameMode = gm
                sender.sendMessage("<green>You are now in <yellow>${gm.name.lowercase()} <green>mode.")
                Result.SUCCESS
            }

            argument(
                name = "player",
                inputType = ArgumentInputType.PLAYER,
                dynamicProvider = Provider.PLAYERS,
                permission = Permission("minepact.gamemode.${gm.name.lowercase()}.others"),
                optional = true
            ) { executes { player, args ->
                val target = args[0].value as Player
                if (target.asPlayer()!!.gameMode == gm) {
                    player.sendMessage("<red>${target.data.name} is already in <yellow>${gm.name.lowercase()} <red>mode.")
                    return@executes Result.SUCCESS
                }
                target.asPlayer()!!.gameMode = gm
                if (player != target) player.sendMessage("<green>${target.data.name} is now in <yellow>${gm.name.lowercase()} <green>mode.")
                target.sendMessage("<green>You are now in <yellow>${gm.name.lowercase()} <green>mode.")
                return@executes Result.SUCCESS
            } }
        }
    }
}