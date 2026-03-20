package net.minepact.core.global.commands.staff

import net.minepact.api.command.CommandUsage
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Argument
import net.minepact.api.player.Player
import net.minepact.api.permissions.Permission
import net.minepact.api.server.ServerType
import org.bukkit.craftbukkit.entity.CraftPlayer
import net.minepact.api.command.Command

class VanishCommand : Command(
    server = ServerType.GLOBAL,
    name = "vanish",
    description = "Turns a player invisible to other players.",
    aliases = mutableListOf("v"),
    permission = Permission("minepact.staff.vanish"),
    usage = CommandUsage(label = "vanish", arguments = emptyList()),
    cooldown = 1.0,
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