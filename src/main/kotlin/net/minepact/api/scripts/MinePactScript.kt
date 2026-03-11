package net.minepact.api.scripts

import net.minepact.Main
import net.minepact.api.command.Command
import net.minepact.api.command.CommandUsage
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Argument
import net.minepact.api.player.Player
import net.minepact.api.permissions.Permission
import net.minepact.api.server.ServerType
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.*
import kotlin.script.experimental.jvm.*

/**
 * Base class for all MinePact scripts (.minepact.kts files).
 *
 * --- IDE SUPPORT ---
 * IntelliJ IDEA detects this @KotlinScript annotation automatically.
 * To get full autocomplete in your scripts, open them in a project that has
 * MinePact as a compile dependency (see companion-project/build.gradle.kts).
 *
 * --- AVAILABLE IN EVERY SCRIPT (no import needed) ---
 *  - plugin          → MinePactPlugin instance
 *  - server          → Bukkit Server
 *  - logger          → Plugin logger
 *  - scriptName      → This script's logical name
 *  - provide<T>()    → Register a service for other scripts
 *  - require<T>()    → Retrieve a service from another script (throws if missing)
 *  - optional<T>()   → Retrieve a service, or null if not loaded yet
 *  - listen<T>()     → Register a Bukkit event listener
 *  - schedule()      → Run a task on the main thread
 *  - scheduleAsync() → Run a task asynchronously
 */
@KotlinScript(
    displayName = "MinePact Script",
    fileExtension = "minepact.kts",
    compilationConfiguration = MinePactScriptCompilationConfig::class,
    evaluationConfiguration = MinePactScriptEvalConfig::class
)
abstract class MinePactScript(val api: ScriptAPI) {
    val plugin get() = api.plugin
    val server get() = api.bukkitServer
    val logger get() = api.logger
    val scriptName get() = api.scriptName

    inline fun <reified T : Any> provide(instance: T) = api.provide(T::class, instance)
    inline fun <reified T : Any> require(): T = api.require(T::class)
    inline fun <reified T : Any> optional(): T? = api.optional(T::class)
    inline fun <reified T : Event> listen(
        priority: EventPriority = EventPriority.NORMAL,
        ignoreCancelled: Boolean = false,
        crossinline handler: (T) -> Unit
    ) = api.listen<T>(priority, ignoreCancelled, handler)
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
    ) = api.command(server, name, description, aliases, permission, usage, cooldown, log, handler)

    fun schedule(
        delay: Long = 0L,
        period: Long = -1L,
        block: () -> Unit
    ) = api.schedule(delay, period, block)
    fun scheduleAsync(
        delay: Long = 0L,
        period: Long = -1L,
        block: () -> Unit
    ) = api.scheduleAsync(delay, period, block)
}