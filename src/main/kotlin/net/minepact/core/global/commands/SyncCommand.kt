package net.minepact.core.global.commands

import net.minepact.api.command.Command
import net.minepact.api.command.CommandUsage
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Argument
import net.minepact.api.data.repository.SyncCodeRepository
import net.minepact.api.messages.send
import net.minepact.api.player.PlayerRegistry
import net.minepact.api.player.discord.SyncData
import net.minepact.api.player.discord.generateSyncCode
import net.minepact.api.permissions.Permission
import org.bukkit.entity.Player

class SyncCommand : Command(
    name = "sync",
    description = "Syncs the player with their discord account.",
    permission = Permission("minepact.sync"),
    usage = CommandUsage(label = "sync", arguments = listOf()),
    playerOnly = true,
) {
    override fun execute(
        sender: net.minepact.api.player.Player,
        args: MutableList<Argument<*>>
    ): Result {
        if (sender.data.discordId != "") {
            sender.sendMessage("<red>You are already synced!")
            return Result.FAILURE
        }
        if (SyncCodeRepository.findByUUID(sender.data.uuid).get() != null) {
            val code: String = SyncCodeRepository.findByUUID(sender.data.uuid).get()!!.code
            sender.sendMessage("<red>You already have a sync code! Your code is <white><click:copy_to_clipboard:$code><hover:show_text:'<gray>Click to copy'>$code</hover></click><red>!")
            return Result.SUCCESS
        }

        val code: String = generateSyncCode()

        sender.sendMessage("")
        sender.sendMessage("<green>Your sync code is <white><click:copy_to_clipboard:$code><hover:show_text:'<gray>Click to copy'>$code</hover></click>")
        sender.sendMessage("<green>In <yellow>/discord<green>, type <yellow>/sync $code <green>to sync your accounts.")
        sender.sendMessage("")

        SyncCodeRepository.insertWithoutUpdate(SyncData(sender.data.uuid, code))

        return Result.SUCCESS
    }
}