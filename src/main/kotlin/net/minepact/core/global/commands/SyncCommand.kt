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
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SyncCommand : Command(
    name = "sync",
    description = "Syncs the player with their discord account.",
    permission = "minepact.sync",
    usage = CommandUsage(label = "sync", arguments = listOf()),
    playerOnly = true,
) {
    override fun execute(
        sender: CommandSender,
        args: MutableList<Argument<*>>
    ): Result {
        PlayerRegistry.get(sender.name).thenAccept {
            if (it.data.discordId != "") {
                sender.send("<red>You are already synced!")
                return@thenAccept
            }
            if (SyncCodeRepository.findByUUID(it.data.uuid).get() != null) {
                val code: String = SyncCodeRepository.findByUUID(it.data.uuid).get()!!.code
                sender.send("<red>You already have a sync code! Your code is <white><click:copy_to_clipboard:$code><hover:show_text:'<gray>Click to copy'>$code</hover></click><red>!")
                return@thenAccept
            }

            val code: String = generateSyncCode()

            sender.send("")
            sender.send("<green>Your sync code is <white><click:copy_to_clipboard:$code><hover:show_text:'<gray>Click to copy'>$code</hover></click>")
            sender.send("<green>In <yellow>/discord<green>, type <yellow>/sync $code <green>to sync your accounts.")
            sender.send("")

            SyncCodeRepository.insertWithoutUpdate(SyncData((sender as Player).uniqueId, code))
        }
        return Result.SUCCESS
    }
}