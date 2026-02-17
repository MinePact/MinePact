package net.minepact.core.global.commands

import net.minepact.api.command.Command
import net.minepact.api.command.CommandUsage
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Argument
import net.minepact.api.command.arguments.ArgumentInputType
import net.minepact.api.command.arguments.ExpectedArgument
import net.minepact.api.schedular.EventSchedular
import net.minepact.api.schedular.TimeInterval
import net.minepact.api.schedular.TimedEvent
import net.minepact.core.global.events.timed.RestartEvent
import org.bukkit.command.CommandSender

class RestartCommand : Command(
    name = "restart",
    description = "Restart the server!",
    permission = "minepact.admin.restart",
    aliases = mutableListOf(""),
    usage = CommandUsage(
        label = "restart", arguments = listOf(
            ExpectedArgument(
                name = "cancelled",
                potentialValues = listOf("cancel"),
                inputType = ArgumentInputType.STRING,
                optional = true,
            )
        )
    ),
    cooldown = 1.0,
    log = true
) {
    override fun execute(
        sender: CommandSender, args: MutableList<Argument<*>>
    ): Result {
        if (args.isNotEmpty()) {
            if (args[0].value == "cancel") {
                EventSchedular.RUNNING_EVENTS.filter { (_, value) -> value.javaClass == RestartEvent().javaClass }.values.last()
                    .cancel()
                return Result.SUCCESS
            }
        }

        val event: TimedEvent = RestartEvent(5 * TimeInterval.SECOND)
        EventSchedular.execute(event)
        return Result.SUCCESS
    }
}