package net.minepact.core.global.commands.staff.punishment

import net.minepact.api.command.Command
import net.minepact.api.command.CommandUsage
import net.minepact.api.command.Provider
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Argument
import net.minepact.api.command.arguments.ArgumentInputType
import net.minepact.api.command.arguments.ExpectedArgument
import net.minepact.api.data.repository.PunishmentRepository
import net.minepact.api.messages.send
import net.minepact.api.misc.formatDateShort
import net.minepact.api.misc.formatDuration
import net.minepact.api.player.Player
import net.minepact.api.permissions.Permission
import net.minepact.api.punishment.Punishment
import net.minepact.api.punishment.PunishmentType

class HistoryCommand : Command(
    name = "history",
    description = "Retrieves a player's punishment history.",
    permission = Permission("minepact.punishments.hist"),
    aliases = mutableListOf(),
    usage = CommandUsage(
        label = "history", arguments = listOf(
            ExpectedArgument(name = "player", dynamicProvider = Provider.PLAYERS),
            ExpectedArgument(name = "limit", potentialValues = listOf("10", "20", "30"), inputType = ArgumentInputType.STRING, optional = true)
        )
    ),
    cooldown = 1.0
) {
    override fun execute(
        sender: Player,
        args: MutableList<Argument<*>>
    ): Result {
        val target: String = args[0].value as String
        val player = net.minepact.api.player.PlayerRegistry.get(target).get()
        val limit: Int = Integer.parseInt(args[1].value as String? ?: "10")

        var punishments: List<Punishment> = PunishmentRepository.findByTarget(player.data.uuid).get()
        sender.sendMessage("")
        sender.sendMessage("<green>Last $limit punishments for <white>$target<green>:")
        if (punishments.isEmpty()) sender.sendMessage("<yellow>| <red>No punishments found :(")

        punishments = punishments.subList(0, minOf(limit, punishments.size))
        format(punishments.sortedByDescending { it.punishedAt }).forEach { sender.sendMessage(it) }

        return Result.SUCCESS
    }

    fun format(punishments: List<Punishment>): List<String> {
        return punishments.map { punishment ->
            TODO()
        }
    }
}