package net.minepact.core.global.commands.staff.punishment.experimental

import net.minepact.api.command.Command
import net.minepact.api.command.CommandUsage
import net.minepact.api.command.Provider
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Argument
import net.minepact.api.command.arguments.ExpectedArgument
import net.minepact.api.player.Player
import net.minepact.api.permissions.Permission

class PunishCommand : Command(
    name = "punish",
    description = "Punish a player",
    permission = Permission("minepact.staff.punish"),
    usage = CommandUsage(label = "punish", arguments = listOf(
        ExpectedArgument(name = "player", dynamicProvider = Provider.PLAYERS),
    )),
    cooldown = 5.0,
    playerOnly = true
) {
    override fun execute(
        sender: Player,
        args: MutableList<Argument<*>>
    ): Result {

        return Result.SUCCESS
    }
}