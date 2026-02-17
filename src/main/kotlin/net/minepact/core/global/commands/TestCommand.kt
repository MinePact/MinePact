package net.minepact.core.global.commands

import io.papermc.paper.command.brigadier.argument.ArgumentTypes.player
import net.minepact.Main
import net.minepact.api.command.Command
import net.minepact.api.command.CommandUsage
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Argument
import net.minepact.api.command.arguments.ArgumentInputType
import net.minepact.api.command.arguments.ExpectedArgument
import net.minepact.api.messages.send
import net.minepact.core.global.menues.ExampleMenu
import org.bukkit.Particle
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector

class TestCommand : Command(
    name = "test",
    description = "A testing command for the developers.",
    permission = "minepact.dev.test",
    aliases = mutableListOf(""),
    usage = CommandUsage(
        label = "test",
        arguments = emptyList()
    ),
    cooldown = 1.0,
    playerOnly = true
) {
    override fun execute(
        sender: CommandSender,
        args: MutableList<Argument<*>>
    ): Result {
        ExampleMenu().open(sender as Player)
        return Result.SUCCESS
    }
}