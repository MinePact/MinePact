package net.minepact.api.command.dsl

import net.minepact.Main
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Argument
import net.minepact.api.command.arguments.parseArgument
import net.minepact.api.logging.LogInfo
import net.minepact.api.logging.LogType
import net.minepact.api.messages.send
import net.minepact.api.player.asPlayer
import net.minepact.api.server.ServerType
import org.bukkit.Bukkit
import org.bukkit.command.Command as BukkitCommand
import org.bukkit.command.CommandMap
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import java.util.UUID
import net.minepact.api.discord.Webhooks.LOGGING_WEBHOOK

/**
 * A class responsible for the registration and function of all commands when ran.
 * Handles permissions, cooldowns, argument parsing, and tab completion.
 *
 * @author dankenyon - 22/02/26
 */
class CommandRegister {
    private val COMMANDS: MutableList<Command> = mutableListOf()
    private val RAN_COMMANDS: MutableMap<CommandSender, MutableMap<Command, Long>> = mutableMapOf()

    fun register(command: Command) {
        val root = command.root

        val bukkitCommand = object : BukkitCommand(
            root.name,
            root.description,
            command.buildUsage(),
            root.aliases
        ) {
            override fun execute(sender: CommandSender, label: String, args: Array<String>): Boolean {
                if (!sender.asPlayer().hasPermission(root.permission!!)
                    && !sender.hasPermission("minepact.command.bypass")
                    && !sender.isOp
                    && sender !is ConsoleCommandSender
                ) {
                    sender.send("<red>You do not have permission to execute this command.")
                    return true
                }

                if (root.playerOnly && sender !is Player) {
                    sender.send("<red>This command can only be used by players.")
                    return true
                }

                if (root.cooldown > 0 && !sender.hasPermission("minepact.cooldown.bypass")) {
                    val lastRan = RAN_COMMANDS[sender]?.get(command)
                    if (lastRan != null) {
                        val cooldownMillis = (root.cooldown * 1000).toLong()
                        val elapsed = System.currentTimeMillis() - lastRan
                        if (elapsed < cooldownMillis) {
                            val timeLeft = (cooldownMillis - elapsed) / 1000.0
                            sender.send("<red>You must wait ${timeLeft}s before using this command again.")
                            return true
                        }
                    }
                }

                val parsedArgs = mutableListOf<Argument<*>>()
                val result = dispatch(sender, root, args, 0, parsedArgs)

                if (result == null) {
                    sender.send("<red>Usage:\n${command.buildUsage(args)}")
                    return true
                }

                RAN_COMMANDS.getOrPut(sender) { mutableMapOf() }[command] = System.currentTimeMillis()

                if (root.log) {
                    LOGGING_WEBHOOK.sendMessage("**${sender.name}** executed: /${root.name} ${args.joinToString(" ")}")
                }

                sender.asPlayer().writeLog(
                    LogInfo(
                        serverId = Main.SERVER.info.uuid,
                        senderId = (sender as? Player)?.uniqueId ?: UUID(0, 0),
                        type = LogType.COMMAND,
                        timestamp = System.currentTimeMillis(),
                        content = "/${root.name} ${args.joinToString(" ")}",
                        suspicious = root.log
                    )
                )

                return result == Result.SUCCESS
            }

            override fun tabComplete(sender: CommandSender, alias: String, args: Array<String>): MutableList<String> =
                tabCompletions(sender, root, args, 0)
        }

        if (command.server == Main.SERVER.info.type || command.server == ServerType.GLOBAL) {
            getCommandMap().register(Main.instance.name.lowercase(), bukkitCommand)
            COMMANDS.add(command)
            Main.instance.logger.info("[CommandRegister] Registered ${command.javaClass.name}!")
        }
    }

    private fun dispatch(
        sender: CommandSender,
        node: CommandNode,
        args: Array<String>,
        index: Int,
        parsedArgs: MutableList<Argument<*>>
    ): Result? {
        if (index >= args.size) {
            // Check playerOnly at execution time, not just traversal time
            if (node.playerOnly && sender !is Player) {
                sender.send("<red>This can only be used by players.")
                return Result.FAILURE
            }
            return node.executor?.invoke(sender.asPlayer(), parsedArgs)
        }

        val current = args[index]

        node.children
            .filter { it.type == CommandNode.Type.LITERAL }
            .firstOrNull { it.matches(current) }
            ?.let { literalChild ->
                if (literalChild.permission != null
                    && !sender.asPlayer().hasPermission(literalChild.permission)
                    && !sender.hasPermission("minepact.command.bypass")
                    && !sender.isOp
                    && sender !is ConsoleCommandSender
                ) {
                    sender.send("<red>You do not have permission to execute this subcommand.")
                    return Result.FAILURE
                }
                // playerOnly on literals is still checked at traversal for early rejection
                if (literalChild.playerOnly && sender !is Player) {
                    sender.send("<red>This subcommand can only be used by players.")
                    return Result.FAILURE
                }
                return dispatch(sender, literalChild, args, index + 1, parsedArgs)
            }

        node.children
            .firstOrNull { it.type == CommandNode.Type.ARGUMENT }
            ?.let { argChild ->
                val exp = argChild.argument!!
                if (exp.permission != null && !sender.hasPermission(exp.permission)) return null
                if (exp.senderFilter?.invoke(sender) == false) return null

                val parsed = parseArgument(current, exp)
                if (parsed != null) {
                    parsedArgs.add(parsed)
                    return dispatch(sender, argChild, args, index + 1, parsedArgs)
                }

                if (exp.optional) {
                    if (argChild.playerOnly && sender !is Player) {
                        sender.send("<red>This can only be used by players.")
                        return Result.FAILURE
                    }
                    return argChild.executor?.invoke(sender.asPlayer(), parsedArgs)
                }
            }

        return null
    }

    private fun tabCompletions(
        sender: CommandSender,
        node: CommandNode,
        args: Array<String>,
        index: Int
    ): MutableList<String> {
        val current = args.getOrElse(index) { "" }.lowercase()

        if (index >= args.size - 1) {
            val completions = mutableListOf<String>()

            node.children
                .filter { it.type == CommandNode.Type.LITERAL }
                .filter { child ->
                    child.permission == null
                            || sender.asPlayer().hasPermission(child.permission)
                            || sender.hasPermission("minepact.command.bypass")
                            || sender.isOp
                }
                .flatMap { listOf(it.name) + it.aliases }
                .filterTo(completions) { it.lowercase().startsWith(current) }

            node.children
                .filter { it.type == CommandNode.Type.ARGUMENT }
                .forEach { child ->
                    val exp = child.argument!!
                    if (exp.permission != null && !sender.hasPermission(exp.permission)) return@forEach
                    if (exp.senderFilter?.invoke(sender) == false) return@forEach
                    val values = exp.dynamicProvider?.invoke(sender)
                        ?: exp.potentialValues
                        ?: return@forEach
                    values.filterTo(completions) { it.lowercase().startsWith(current) }
                }

            return completions.distinct().toMutableList()
        }

        val input = args[index]

        node.children
            .filter { it.type == CommandNode.Type.LITERAL }
            .firstOrNull { it.matches(input) }
            ?.let { return tabCompletions(sender, it, args, index + 1) }

        node.children
            .firstOrNull { it.type == CommandNode.Type.ARGUMENT }
            ?.let { return tabCompletions(sender, it, args, index + 1) }

        return mutableListOf()
    }

    fun unregister(command: Command) {
        getCommandMap().knownCommands.remove("${Main.instance.name.lowercase()}:${command.name}")
        getCommandMap().knownCommands.remove(command.name)
        COMMANDS.remove(command)
        Main.instance.logger.info("[CommandRegister] Unregistered ${command.javaClass.name}!")
    }

    fun unregister(name: String) {
        getCommandMap().knownCommands.remove("${Main.instance.name.lowercase()}:$name")
        getCommandMap().knownCommands.remove(name)
        val found = COMMANDS.first { it.name == name }
        Main.instance.logger.info("[CommandRegister] Unregistered ${found.javaClass.name}!")
        COMMANDS.removeIf { it.name == name }
    }

    fun all(): List<Command> = COMMANDS
    fun allNames(): List<String> = COMMANDS.map { it.name }

    fun getCommandMap(): CommandMap {
        val field = Bukkit.getServer().javaClass.getDeclaredField("commandMap")
        field.isAccessible = true
        return field.get(Bukkit.getServer()) as CommandMap
    }
}