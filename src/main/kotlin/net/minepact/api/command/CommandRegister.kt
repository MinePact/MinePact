package net.minepact.api.command

import net.minepact.Main
import net.minepact.api.command.arguments.Argument
import net.minepact.api.command.arguments.parseArgument
import net.minepact.api.messages.send
import org.bukkit.Bukkit
import org.bukkit.command.Command as BukkitCommand
import org.bukkit.command.CommandMap
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandRegister {
    private val COMMANDS: MutableList<Command> = mutableListOf()
    private val RAN_COMMANDS: MutableMap<CommandSender, MutableMap<Command, Long>> = mutableMapOf()

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
                    sender.send("<red>You do not have permission to execute this command.")
                    return true
                }

                if (command.playerOnly && sender !is Player) {
                    sender.send("<red>This command can only be used by players.")
                    return true
                }

                val parsedArgs = mutableListOf<Argument<*>>()

                for (index in args.indices) {
                    val expectedArguments = command.usage.arguments

                    if (expectedArguments.isEmpty()) {
                        sender.send("<red>Invalid arguments. Usage: ${command.usage}")
                        return true
                    }
                    val parsed = expectedArguments
                        .filter { expected -> expected.permission?.let(sender::hasPermission) ?: true }
                        .filter { expected -> expected.senderFilter?.invoke(sender) ?: true }
                        .firstNotNullOfOrNull { expected -> parseArgument(args[index], expected) }
                    if (parsed == null) {
                        sender.send("<red>Invalid arguments. Usage: ${command.usage}")
                        return true
                    }

                    parsedArgs.add(parsed)
                }
                val missingRequired = command.usage.arguments
                    .drop(parsedArgs.size)
                    .any { !it.optional }

                if (missingRequired) {
                    sender.send("<red>Invalid arguments. Usage: ${command.usage}")
                    return true
                }

                return try {
                    if (RAN_COMMANDS.contains(sender) && RAN_COMMANDS[sender]!!.contains(command)) {
                        val lastRan = RAN_COMMANDS[sender]!![command]!!
                        val cooldownMillis = (command.cooldown * 1000).toLong()
                        if (System.currentTimeMillis() - lastRan < cooldownMillis) {
                            val timeLeft: Double = ((cooldownMillis - (System.currentTimeMillis() - lastRan)) / 1000.0)
                            sender.send("<red>You must wait $timeLeft seconds before using this command again.")
                            return true
                        }
                    }

                    RAN_COMMANDS[sender] = RAN_COMMANDS.getOrDefault(sender, mutableMapOf())
                        .also { it[command] = System.currentTimeMillis() }
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
                val expected = command.usage.arguments.getOrNull(index) ?: return mutableListOf()

                return sequenceOf(expected)
                    .filter { it.permission?.let(sender::hasPermission) ?: true }
                    .filter { it.senderFilter?.invoke(sender) ?: true }
                    .flatMap { arg ->
                        when {
                            arg.dynamicProvider != null -> arg.dynamicProvider.invoke(sender).asSequence()
                            arg.potentialValues != null -> arg.potentialValues?.asSequence() ?: emptySequence()
                            else -> emptySequence()
                        }
                    }
                    .filter { it.lowercase().startsWith(current) }
                    .distinct()
                    .sorted()
                    .toMutableList()
            }
        }

        getCommandMap().register(Main.instance.name.lowercase(), bukkitCommand)
        COMMANDS.add(command)
        Main.instance.logger.info("[CommandRegister] Registered ${command.javaClass.name}!")
    }

    fun all(): List<Command> = COMMANDS
    fun allNames(): List<String> = COMMANDS.map { it.name }

    private fun getCommandMap(): CommandMap {
        val field = Bukkit.getServer().javaClass.getDeclaredField("commandMap")
        field.isAccessible = true
        return field.get(Bukkit.getServer()) as CommandMap
    }
}
