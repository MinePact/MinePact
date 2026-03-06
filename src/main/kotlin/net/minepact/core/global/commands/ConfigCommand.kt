package net.minepact.core.global.commands

import net.minepact.api.command.Command
import net.minepact.api.command.CommandUsage
import net.minepact.api.command.Provider
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Argument
import net.minepact.api.command.arguments.ExpectedArgument
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
            ExpectedArgument(name = "action", dynamicProvider = Provider.CONFIG_ACTIONS),
            ExpectedArgument(
                name = "config",
                potentialValues = ConfigurationRegistry.configs.keys.map<KClass<*>, String> { it.simpleName!! },
            )
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
        val action = args[0].value as String
        val configName = args[1].value as String

        val configClass = ConfigurationRegistry.configs.keys.firstOrNull { it.simpleName == configName }
        if (configClass == null) {
            sender.sendMessage("<red>Configuration file '$configName' not found.")
            return Result.SUCCESS
        }
        val configInstance = ConfigurationRegistry.configs[configClass]!!

        when (action.lowercase()) {
            "get" -> {
                super.usage = CommandUsage(
                    label = usage.label,
                    arguments = listOf(
                        ExpectedArgument(name = "action", dynamicProvider = Provider.CONFIG_ACTIONS),
                        ExpectedArgument(
                            name = "config",
                            potentialValues = ConfigurationRegistry.configs.keys.map<KClass<*>, String> { it.simpleName!! },
                        ),
                        ExpectedArgument(name = "value", dynamicProvider = Provider.EMPTY)
                    )
                )


            }

            "set" -> {
                // TODO
            }

            "reload" -> {
                ConfigurationRegistry.reload(clazz = configInstance.clazz)
                sender.sendMessage("<green>Configuration file '$configName' reloaded successfully.")
            }
        }
        return Result.SUCCESS
    }
}
