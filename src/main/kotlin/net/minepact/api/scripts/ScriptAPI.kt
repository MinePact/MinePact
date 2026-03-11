package net.minepact.api.scripts

import net.minepact.Main
import net.minepact.api.command.Command
import net.minepact.api.command.CommandUsage
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Argument
import net.minepact.api.command.arguments.ExpectedArgument
import net.minepact.api.player.Player
import net.minepact.api.permissions.Permission
import net.minepact.api.scripts.exceptions.MissingServiceException
import net.minepact.api.scripts.registries.ListenerRegistry
import net.minepact.api.scripts.registries.ScriptServiceRegistry
import net.minepact.api.server.ServerType
import org.bukkit.Server
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.scheduler.BukkitTask
import java.util.logging.Logger
import kotlin.reflect.KClass

class ScriptAPI(
    val plugin: Main,
    val bukkitServer: Server,
    val registry: ScriptServiceRegistry,
    val scriptName: String,
    val listenerRegistry: ListenerRegistry
) {
    val logger: Logger = plugin.logger

    fun <T : Any> provide(type: KClass<T>, instance: T) {
        registry.register(type, instance)
        logger.fine("[ScriptEngine] '$scriptName' provided: ${type.simpleName}")
    }
    fun <T : Any> require(type: KClass<T>): T {
        return registry.get(type) ?: throw MissingServiceException(
                "Script '$scriptName' requires '${type.simpleName}' but it is not registered.\n" +
                "Make sure the script that provides it is declared as a dependency via " +
                "@file:DependsOn(\"...\") or via loadOrder() in bootstrap.minepact.kts."
        )
    }
    fun <T : Any> optional(type: KClass<T>): T? = registry.get(type)

    inline fun <reified T : Event> listen(
        priority: EventPriority = EventPriority.NORMAL,
        ignoreCancelled: Boolean = false,
        crossinline handler: (T) -> Unit
    ): Listener {
        val listener = object : Listener {}
        bukkitServer.pluginManager.registerEvent(
            T::class.java,
            listener,
            priority,
            { _, event -> if (event is T) handler(event) },
            plugin,
            ignoreCancelled
        )
        listenerRegistry.track(scriptName, listener)
        return listener
    }

    inline fun command(
        server: ServerType = ServerType.GLOBAL,
        name: String,
        description: String = "",
        aliases: MutableList<String> = mutableListOf(),
        permission: Permission,
        usage: CommandUsage,
        cooldown: Double = 0.0,
        log: Boolean = false,
        crossinline handler: (sender: Player, args: MutableList<Argument<*>>) -> Result
    ) {
        val cmd: Command = object : Command(
            server = server,
            name = name,
            description = description,
            aliases = aliases,
            permission = permission,
            usage = usage,
            cooldown = cooldown,
            log = log
        ) {
            override fun execute(
                sender: Player,
                args: MutableList<Argument<*>>
            ): Result = handler.invoke(sender, args)
        }

        Main.COMMAND_REGISTRY.unregister(cmd)
        Main.COMMAND_REGISTRY.register(cmd)
    }

    fun schedule(delay: Long = 0L, period: Long = -1L, block: () -> Unit): BukkitTask {
        return if (period > 0L) {
            bukkitServer.scheduler.runTaskTimer(plugin, Runnable(block), delay, period)
        } else {
            bukkitServer.scheduler.runTaskLater(plugin, Runnable(block), delay)
        }
    }
    fun scheduleAsync(delay: Long = 0L, period: Long = -1L, block: () -> Unit): BukkitTask {
        return if (period > 0L) {
            bukkitServer.scheduler.runTaskTimerAsynchronously(plugin, Runnable(block), delay, period)
        } else {
            bukkitServer.scheduler.runTaskLaterAsynchronously(plugin, Runnable(block), delay)
        }
    }
}