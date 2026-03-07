package net.minepact.core.global.commands

import net.minepact.api.command.Command
import net.minepact.api.command.CommandUsage
import net.minepact.api.command.Provider
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Argument
import net.minepact.api.command.arguments.ExpectedArgument
import net.minepact.api.config.custom.ConfigManager
import net.minepact.api.config.custom.ConfigType
import net.minepact.api.config.custom.helper.MinePactConfigType
import net.minepact.api.config.custom.helper.get
import net.minepact.api.config.custom.minepact.MinePactFile
import net.minepact.api.config.experimental.ConfigurationRegistry
import net.minepact.api.player.Player
import net.minepact.api.player.permissions.Permission
import kotlin.reflect.KClass

class ConfigCommand : Command(
    name = "config",
    description = "Allows access to the servers configuration files.",
    usage = CommandUsage(
        label = "config",
        arguments = listOf(

        )
    ),
    permission = Permission("minepact.admin.config"),
    aliases = mutableListOf("cfg", "conf"),
    cooldown = 1.0,
    playerOnly = false
) {
    override fun execute(
        sender: Player,
        args: MutableList<Argument<*>>
    ): Result {
        val file: MinePactFile = ConfigManager.file<MinePactConfigType>("example.mpc")

        val a = file.reader.get<String>("database.username")
        val b = file.reader.get<Int>("database.port")

        sender.sendMessage(a)
        sender.sendMessage("$b")

        return Result.SUCCESS
    }
}
