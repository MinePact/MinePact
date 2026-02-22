package net.minepact.core.global.commands.punishment

import net.minepact.api.command.Command
import net.minepact.api.command.CommandUsage
import net.minepact.api.command.Provider
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Argument
import net.minepact.api.command.arguments.ArgumentInputType
import net.minepact.api.command.arguments.ExpectedArgument
import net.minepact.api.data.repository.PunishmentRepository
import net.minepact.api.messages.send
import net.minepact.api.misc.getLengthFromIdentifier
import org.bukkit.command.CommandSender

class PruneHistoryCommand : Command(
    name = "prune",
    description = "Removes punishments from a player's history that are newer than the specified length.",
    usage = CommandUsage(
        label = "prune", arguments = listOf(
            ExpectedArgument(name = "player", dynamicProvider = Provider.PLAYERS),
            ExpectedArgument(name = "length", inputType = ArgumentInputType.STRING, optional = true),
        )
    ),
    permission = "minepact.punishment.prune-history"
) {
    override fun execute(
        sender: CommandSender,
        args: MutableList<Argument<*>>
    ): Result {
        val targetName: String = args[0].value as String
        val length: String? = if (args.size > 1) args[1].value as String else null
        var time: Long = Long.MAX_VALUE

        if (length != null) {
            time = getLengthFromIdentifier(length)
        }

        PunishmentRepository.findByTarget(targetName).thenAccept { it
            .filter { punishment -> punishment.punishedAt >= System.currentTimeMillis() - time }
            .filter { punishment -> punishment.expiresAt != Long.MIN_VALUE && punishment.expiresAt <= System.currentTimeMillis() }
            .forEach { punishment -> PunishmentRepository.deleteById(punishment.id) }

            sender.send("<green>Pruned ${it.size}${if (length != null) " <grey>[$length]</grey>" else ""} punishments from <white>$targetName's <green>history.")
        }
        return Result.SUCCESS
    }
}