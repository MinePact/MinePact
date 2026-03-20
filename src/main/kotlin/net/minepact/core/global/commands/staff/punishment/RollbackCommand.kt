package net.minepact.core.global.commands.staff.punishment

import net.minepact.api.command.CommandUsage
import net.minepact.api.command.Provider
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Argument
import net.minepact.api.command.arguments.ArgumentInputType
import net.minepact.api.command.arguments.ExpectedArgument
import net.minepact.api.data.repository.PunishmentRepository
import net.minepact.api.messages.send
import net.minepact.api.misc.getLengthFromIdentifier
import net.minepact.api.player.Player
import net.minepact.api.player.PlayerRegistry
import net.minepact.api.permissions.Permission
import kotlin.collections.filter
import net.minepact.api.command.Command

class RollbackCommand : Command(
    name = "rollback",
    description = "Removes punishments from a staff's history that are newer than the specified length.",
    usage = CommandUsage(
        label = "rollback", arguments = listOf(
            ExpectedArgument(name = "player", dynamicProvider = Provider.PLAYERS),
            ExpectedArgument(name = "length", inputType = ArgumentInputType.STRING, optional = true),
        )
    ),
    permission = Permission("minepact.punishments.rollback")
) {
    override fun execute(
        sender: Player,
        args: MutableList<Argument<*>>
    ): Result {
        val targetName: String = args[0].value as String
        val target: Player = PlayerRegistry.get(targetName).get()
        val length: String? = if (args.size > 1) args[1].value as String else null
        var time: Long = Long.MAX_VALUE

        if (length != null) {
            time = getLengthFromIdentifier(length)
        }

        PunishmentRepository.findByIssuer(target.data.uuid).thenAccept { it
            .filter { punishment -> punishment.punishedAt >= System.currentTimeMillis() - time }
            .forEach { punishment -> PunishmentRepository.deleteById(punishment.id) }

            sender.sendMessage("<green>Rolled back${it.size}${if (length != null) " <grey>[$length]</grey>" else ""} punishments from <white>$targetName's <green>punishments.")
        }
        return Result.SUCCESS
    }
}