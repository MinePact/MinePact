package net.minepact.core.global.commands

import net.minepact.api.command.Command
import net.minepact.api.command.CommandUsage
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Argument
import net.minepact.api.item.Item
import net.minepact.api.messages.FormatParser
import net.minepact.api.player.permissions.Permission
import org.bukkit.Material
import org.bukkit.entity.Player

class TestCommand : Command(
    name = "test",
    description = "A testing command for the developers.",
    permission = Permission("minepact.dev.test"),
    aliases = mutableListOf(""),
    usage = CommandUsage(
        label = "test",
        arguments = listOf()
    ),
    cooldown = 1.0,
    playerOnly = true
) {
    override fun execute(
        sender: net.minepact.api.player.Player,
        args: MutableList<Argument<*>>
    ): Result {



        return Result.SUCCESS
    }
}