package net.minepact.core.global.commands

import net.minepact.api.command.Command
import net.minepact.api.command.CommandUsage
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Argument
import net.minepact.api.command.arguments.ExpectedArgument
import net.minepact.api.player.Player
import net.minepact.api.permissions.Permission
import net.minepact.api.scheduler.EventScheduler
import net.minepact.core.global.events.timed.RestartEvent

class RestartCommand : Command(
    name = "restart",
    description = "Restart the server!",
    permission = Permission("minepact.admin.restart"),
    aliases = mutableListOf(),
    usage = CommandUsage(
        label = "restart",
        arguments = listOf(
            ExpectedArgument(
                name = "seconds",
                potentialValues = listOf("5s", ),
                optional = true
            )
        )
    )
) {
    override fun execute(sender: Player, args: MutableList<Argument<*>>): Result {
        val active: RestartEvent? = EventScheduler.RUNNING_EVENTS.values
            .filterIsInstance<RestartEvent>()
            .firstOrNull()

        if (args.isNotEmpty() && args[0].value.toString().equals("cancel", ignoreCase = true)) {
            active?.cancel()
            sender.sendMessage("Restart has been cancelled.")
            return Result.SUCCESS
        }

        val waitTime: Long = if (args.isNotEmpty()) args[0].value.toString().toLongOrNull() ?: 0L else 0L
        active?.cancel()

        val restartEvent = if (waitTime > 0) RestartEvent(waitTime * 1000L)
        else RestartEvent()

        EventScheduler.execute(restartEvent)
        return Result.SUCCESS
    }
}