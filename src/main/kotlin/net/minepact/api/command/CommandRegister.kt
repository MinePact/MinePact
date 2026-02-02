package net.minepact.api.command

import net.minepact.Main
import net.minepact.api.command.arguments.Argument
import net.minepact.api.command.arguments.parseArgument
import org.bukkit.Bukkit
import org.bukkit.command.Command as BukkitCommand
import org.bukkit.command.CommandMap
import org.bukkit.command.CommandSender

class CommandRegister {
    private val commands: MutableList<Command> = mutableListOf()

    fun register(command: Command) {
        val bukkitCommand = object : BukkitCommand(
            command.name,
            command.description,
            command.usage.toString(),
            command.aliases
        ) {
            override fun execute(
                sender: CommandSender,
                label: String,
                args: Array<String>
            ): Boolean {
                if (!sender.hasPermission(command.permission)) {
                    sender.sendMessage("<red>You do not have permission to execute this command.")
                    return true
                }

                if (command.playerOnly && sender !is org.bukkit.entity.Player) {
                    sender.sendMessage("<red>This command can only be used by players.")
                    return true
                }

                val parsedArgs = mutableListOf<Argument<*>>()

                for (index in args.indices) {
                    val expectedArguments = command.chatComplete(index)

                    if (expectedArguments.isEmpty()) {
                        sender.sendMessage("<red>Invalid arguments. Usage: ${command.usage}")
                        return true
                    }
                    val parsed = expectedArguments
                        .filter { expected -> expected.permission?.let(sender::hasPermission) ?: true }
                        .filter { expected -> expected.senderFilter?.invoke(sender) ?: true }
                        .firstNotNullOfOrNull { expected -> parseArgument(args[index], expected) }
                    if (parsed == null) {
                        sender.sendMessage("<red>Invalid arguments. Usage: ${command.usage}")
                        return true
                    }

                    parsedArgs.add(parsed)
                }
                val missingRequired = command.chatComplete(parsedArgs.size).any { !it.optional }
                if (missingRequired) {
                    sender.sendMessage("<red>Invalid arguments. Usage: ${command.usage}")
                    return true
                }

                return try {
                    command.execute(sender, parsedArgs) == Result.SUCCESS
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }

            override fun tabComplete(
                sender: CommandSender,
                alias: String,
                args: Array<String>
            ): MutableList<String> {
                val index = args.size - 1
                val current = args.lastOrNull()?.lowercase() ?: ""

                return command.chatComplete(index)
                    .filter { expected -> expected.permission?.let(sender::hasPermission) ?: true }
                    .filter { expected -> expected.senderFilter?.invoke(sender) ?: true }
                    .flatMap { expected ->
                            expected.dynamicProvider?.invoke(sender)
                            ?: expected.potentialValues
                    }
                    .filter { suggestion -> suggestion.lowercase().startsWith(current) }
                    .distinct()
                    .sorted()
                    .toMutableList()
            }
        }

        getCommandMap().register(Main.instance.name.lowercase(), bukkitCommand)
        commands.add(command)
    }

    fun all(): List<Command> = commands
    fun allNames(): List<String> = commands.map { it.name }

    private fun getCommandMap(): CommandMap {
        val field = Bukkit.getServer().javaClass.getDeclaredField("commandMap")
        field.isAccessible = true
        return field.get(Bukkit.getServer()) as CommandMap
    }
}
