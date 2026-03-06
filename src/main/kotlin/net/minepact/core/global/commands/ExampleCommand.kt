package net.minepact.core.global.commands

import net.minepact.api.command.Command
import net.minepact.api.command.CommandUsage
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Argument
import net.minepact.api.player.Player
import net.minepact.api.player.permissions.Permission
import net.minepact.api.server.ServerType

class Command : Command(
    server = ServerType.GLOBAL,
    name = "",
    description = "",
    aliases = mutableListOf(""),
    permission = Permission(""),
    usage = CommandUsage(label = "", arguments = emptyList()),
    cooldown = 0.0,
    log = false,
    maxArgs = 1
) {
    override fun execute(
        sender: Player,
        args: MutableList<Argument<*>>
    ): Result {

        return Result.SUCCESS
    }
}