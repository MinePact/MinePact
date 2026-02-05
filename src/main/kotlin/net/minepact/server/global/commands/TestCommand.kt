package net.minepact.server.global.commands

import net.minepact.Main
import net.minepact.api.command.Command
import net.minepact.api.command.CommandUsage
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Argument
import net.minepact.api.command.arguments.ArgumentInputType
import net.minepact.api.command.arguments.ExpectedArgument
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
        label = "test", arguments = mutableListOf(
            ExpectedArgument(
                name = "a",
                inputType = ArgumentInputType.DOUBLE,
                potentialValues = listOf("1", "2", "3", "4", "5")
            ),
            ExpectedArgument(
                name = "b",
                inputType = ArgumentInputType.DOUBLE,
                potentialValues = listOf("1", "2", "3", "4", "5")
            ),
            ExpectedArgument(
                name = "c",
                inputType = ArgumentInputType.DOUBLE,
                potentialValues = listOf("1", "2", "3", "4", "5")
            ),
            ExpectedArgument(
                name = "d",
                inputType = ArgumentInputType.DOUBLE,
                potentialValues = listOf("1", "2", "3", "4", "5")
            )
        )
    ),
    cooldown = 30.0
) {
    override fun execute(
        sender: CommandSender,
        args: MutableList<Argument<*>>
    ): Result {
        val player = sender as Player
        val world = player.world

        val a = args[0].value as Double
        val b = args[1].value as Double
        val c = args[2].value as Double
        val d = args[3].value as Double

        val forward = player.location.direction.normalize()
        val globalUp = Vector(0, 1, 0)

        val right = forward.clone().crossProduct(globalUp).normalize()
        val up = right.clone().crossProduct(forward).normalize()

        val origin = player.eyeLocation.clone().add(forward.clone().multiply(5.0)).subtract(0.0, 3.0, 0.0)

        var x = -5.0
        while (x <= 5.0) {
            val y = a * x * x * x + b * x * x + c * x + d

            val point = origin.clone()
                .add(right.clone().multiply(x))
                .add(up.clone().multiply(y))

            object : BukkitRunnable() {
                var ticks = 0

                override fun run() {
                    if (ticks >= 600) {
                        cancel()
                        return
                    }
                    world.spawnParticle(
                        Particle.END_ROD,
                        point,
                        1,
                        0.0, 0.0, 0.0, 0.0
                    )
                    ticks++
                }

            }.runTaskTimer(Main.Companion.instance, 0L, 1L)
            x += 0.1
        }

        return Result.SUCCESS
    }
}