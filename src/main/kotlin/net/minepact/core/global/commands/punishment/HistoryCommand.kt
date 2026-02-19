package net.minepact.core.global.commands.punishment

import net.kyori.adventure.text.minimessage.MiniMessage
import net.minepact.Main
import net.minepact.api.command.Command
import net.minepact.api.command.CommandUsage
import net.minepact.api.command.Provider
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Argument
import net.minepact.api.command.arguments.ArgumentInputType
import net.minepact.api.command.arguments.ExpectedArgument
import net.minepact.api.data.repository.PunishmentRepository
import net.minepact.api.messages.send
import net.minepact.api.misc.formatDate
import net.minepact.api.misc.formatDateShort
import net.minepact.api.misc.formatDuration
import net.minepact.api.punishment.Punishment
import net.minepact.api.punishment.PunishmentType
import org.bukkit.command.CommandSender

class HistoryCommand : Command(
    name = "history",
    description = "Retrieves a player's punishment history.",
    permission = "minepact.punishments.history",
    aliases = mutableListOf(),
    usage = CommandUsage(
        label = "history", arguments = listOf(
            ExpectedArgument(name = "player", dynamicProvider = Provider.PLAYERS),
            ExpectedArgument(name = "limit", potentialValues = listOf("10", "20", "30"), inputType = ArgumentInputType.INTEGER, optional = true)
        )
    ),
    cooldown = 1.0
) {
    override fun execute(
        sender: CommandSender,
        args: MutableList<Argument<*>>
    ): Result {
        val repo: PunishmentRepository = Main.PUNISHMENT_REPOSITORY
        val target: String = args[0].value as String

        val punishments: List<Punishment> = repo.findByTarget(target).get()
        sender.send("<green>History for <white>$target<green>:")
        if (punishments.isEmpty()) sender.send("<yellow>| <red>No punishments found! :(")
        format(punishments.sortedByDescending { it.punishedAt }).forEach { sender.send(it) }

        return Result.SUCCESS
    }

    fun format(punishments: List<Punishment>): List<String> {
        return punishments.map { punishment -> "<yellow>| <grey>[<white>${formatDateShort(punishment.punishedAt)}<grey>] <green>${if(punishment.type == PunishmentType.BAN) "Banned" else if (punishment.type == PunishmentType.MUTE) "Muted" else "Warned"} <green>by <white>${punishment.issuerName} <green>for <white>${punishment.reason} <green>for <white>${formatDuration(punishment.expiresAt - punishment.punishedAt)}<green>. ${if (System.currentTimeMillis() > punishment.expiresAt) "<dark_grey>[<grey>Expired<dark_grey>]" else ""}" }
    }
}