package net.minepact.core.global.commands.gamemode

import net.minepact.api.command.dsl.Command
import net.minepact.api.command.Provider
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.ArgumentInputType
import net.minepact.api.permissions.Permission
import net.minepact.api.player.Player
import org.bukkit.GameMode

class CreativeCommand : Command() {
    init {
        command("creative") {
            description = "Test a command"
            permission = Permission("minepact.gamemode.creative")
            aliases = mutableListOf("gmc")
            playerOnly = true

            executes { sender, _ ->
                if (sender.asPlayer()!!.gameMode == GameMode.CREATIVE) {
                    sender.sendMessage("<red>You are already in <yellow>creative <red>mode.")
                    Result.SUCCESS
                }
                sender.asPlayer()!!.gameMode = GameMode.CREATIVE
                sender.sendMessage("<green>You are now in <yellow>creative <green>mode.")
                Result.SUCCESS
            }

            argument(
                name = "player",
                inputType = ArgumentInputType.PLAYER,
                dynamicProvider = Provider.PLAYERS,
                permission = Permission("minepact.gamemode.creative.others")
            ) {
                playerOnly = false

                executes { player, args ->
                    val target = args[0].value as Player
                    if (target.asPlayer()!!.gameMode == GameMode.CREATIVE) {
                        player.sendMessage("<red>${target.data.name} is already in <yellow>creative <red>mode.")
                        Result.SUCCESS
                    }
                    target.asPlayer()!!.gameMode = GameMode.CREATIVE
                    if (player != target) player.sendMessage("<green>${target.data.name} is now in <yellow>creative <green>mode.")
                    target.sendMessage("<green>You are now in <yellow>creative <green>mode.")
                    Result.SUCCESS
                }
            }
        }
    }
}