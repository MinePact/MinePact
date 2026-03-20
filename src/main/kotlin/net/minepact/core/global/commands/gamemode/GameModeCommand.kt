package net.minepact.core.global.commands.gamemode

import net.minepact.api.command.Provider
import net.minepact.api.command.Result
import net.minepact.api.permissions.Permission
import org.bukkit.GameMode
import net.minepact.api.command.arguments.ArgumentInputType
import net.minepact.api.command.dsl.Command
import net.minepact.api.player.Player

class GameModeCommand : Command() {
    init {
        command("gamemode") {
            description = "Switches the player's gamemode."
            permission = Permission("minepact.gamemode")
            aliases = mutableListOf("gm")
            playerOnly = true

            argument(
                name = "gamemode",
                inputType = ArgumentInputType.GAMEMODE,
                dynamicProvider = Provider.GAMEMODES,
                optional = false
            ) {
                playerOnly = false

                executes { player, args ->
                    var gameMode: GameMode = try {
                        args[0].value as GameMode
                    }catch(e: Exception) {
                        player.sendMessage("<red>Invalid gamemode. Defaulting to survival.")
                        GameMode.SURVIVAL
                    }

                    if (player.asPlayer()!!.gameMode == gameMode) {
                        player.sendMessage("<red>You are already in <yellow>${gameMode.name.lowercase()} <red>mode.")
                        return@executes Result.SUCCESS
                    }
                    player.asPlayer()!!.gameMode = gameMode
                    player.sendMessage("<green>You are now in <yellow>${gameMode.name.lowercase()} <green>mode.")
                    Result.SUCCESS
                }

                argument(
                    name = "player",
                    inputType = ArgumentInputType.PLAYER,
                    dynamicProvider = Provider.PLAYERS,
                    permission = Permission("minepact.gamemode.others"),
                    optional = true
                ) { executes { player, args ->
                    var gameMode: GameMode = try {
                        args[0].value as GameMode
                    }catch(e: Exception) {
                        player.sendMessage("<red>Invalid gamemode. Defaulting to survival.")
                        GameMode.SURVIVAL
                    }

                    val target = args[1].value as Player
                    if (target.asPlayer()!!.gameMode == gameMode) {
                        player.sendMessage("<red>${target.data.name} is already in <yellow>${gameMode.name.lowercase()} <red>mode.")
                        return@executes Result.SUCCESS
                    }
                    target.asPlayer()!!.gameMode = gameMode
                    if (player != target) {
                        player.sendMessage("<green>${target.data.name} is now in <yellow>${gameMode.name.lowercase()} <green>mode.")
                        return@executes Result.SUCCESS
                    }
                    target.sendMessage("<green>You are now in <yellow>${gameMode.name.lowercase()} <green>mode.")
                    Result.SUCCESS
                } }
            }
        }
    }
}