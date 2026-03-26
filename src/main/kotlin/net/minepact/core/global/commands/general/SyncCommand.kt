package net.minepact.core.global.commands.general

import net.minepact.api.command.Provider
import net.minepact.api.command.Result
import net.minepact.api.player.Player
import net.minepact.api.permissions.Permission
import org.bukkit.GameMode
import net.minepact.api.command.arguments.ArgumentInputType
import net.minepact.api.command.dsl.Command
import net.minepact.api.data.repository.SyncCodeRepository
import net.minepact.api.messages.Message
import net.minepact.api.messages.MessageBuilder
import net.minepact.api.messages.helper.msg
import net.minepact.api.misc.formatDuration
import net.minepact.api.player.discord.SyncData
import net.minepact.api.player.discord.generateSyncCode

class SyncCommand : Command() {
    init {
        command("sync") {
            description = "Syncs the player's minecraft and discord account."
            permission = Permission("minepact.sync")
            playerOnly = true

            executes { player, args ->
                if (player.data.discordId != "") {
                    player.sendMessage("<red>You are already synced!")
                    return@executes Result.FAILURE
                }
                if (SyncCodeRepository.findByUUID(player.data.uuid).get() != null) {
                    val code: String = SyncCodeRepository.findByUUID(player.data.uuid).get()!!.code
                    player.sendMessage(msg {
                        +"<red>You already have a sync code! Your code is "
                        text(code) {
                            color(0xFFFFFF)
                            clickCopy("/sync $code")
                            hoverText("<red>Click to copy: <white>/sync $code")
                        }
                        +"<red>!"
                    })
                    return@executes Result.SUCCESS
                }

                val code: String = generateSyncCode()
                player.sendMessage(msg {
                    +"\n"
                    +"<green>Your sync code is "
                    text(code) {
                        color(0xFFFFFF)
                        clickCopy("/sync $code")
                        hoverText("<green>Copy: <white>/sync $code")
                    }
                    +"\n<green>In "
                    text("/discord") {
                        color(0xFFFF00)
                        clickSuggestCommand("/discord")
                        hoverText("<green>Click for the discord link!")
                    }
                    +"<green>, type "
                    text("/sync $code") {
                        color(0xFFFFFF)
                        clickCopy("/sync $code")
                        hoverText("<green>Copy: <white>/sync $code")
                    }
                    +"<green> to sync your accounts."
                })

                SyncCodeRepository.insertWithoutUpdate(SyncData(player.data.uuid, code))
                Result.SUCCESS
            }
        }
    }
}