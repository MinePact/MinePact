package net.minepact

import net.minepact.api.command.dsl.CommandRegister
import net.minepact.api.config.custom.ConfigManager
import net.minepact.api.config.custom.helper.*
import net.minepact.api.data.*
import net.minepact.api.discord.Webhooks.UPDATES_WEBHOOK
import net.minepact.api.event.*
import net.minepact.api.menu.MenuManager
import net.minepact.api.permissions.*
import net.minepact.api.player.*
import net.minepact.api.reflections.*
import net.minepact.api.scheduler.EventScheduler
import net.minepact.api.server.Server
import net.minepact.core.discord.embeds.*
import net.minepact.core.global.events.timed.*
import kotlin.properties.Delegates

class Main : org.bukkit.plugin.java.JavaPlugin() {
    companion object {
        lateinit var instance: Main
        lateinit var COMMAND_REGISTRY: CommandRegister
        lateinit var EVENT_REGISTRY: EventRegister

        lateinit var DATABASE_CONFIG: DatabaseConfig
        lateinit var SERVER: Server
        lateinit var DATABASE: Database

        var RESTARTING: Boolean = false
        var SERVER_START_TIME by Delegates.notNull<Long>()
    }

    override fun onLoad() {
        instance = this

        COMMAND_REGISTRY = CommandRegister()
        EVENT_REGISTRY = EventRegister()
        SERVER_START_TIME = System.currentTimeMillis()

        val config = ConfigManager.file<MinePactConfigType>("config.mpc")
        val reader = config.reader

        DATABASE_CONFIG = DatabaseConfig(
            type = DatabaseProvider.valueOf(reader.get<String>("database.provider")),
            host = reader.get<String>("database.host"),
            port = reader.get<Int>("database.port"),
            database = reader.get<String>("database.name"),
            username = reader.get<String>("database.username"),
            password = reader.get<String>("database.password")
        )
        DATABASE = DatabaseFactory.create(DATABASE_CONFIG)
        findRepositories("net.minepact.api.data.repository").forEach { it.ensureTableExists() }

        SERVER = Server()

        findCommands("net.minepact.core").forEach { COMMAND_REGISTRY.register(it) }
        findEvents("net.minepact").forEach { EVENT_REGISTRY.register(it) }
        registerConfigs("net.minepact.core.global.configs")

        UPDATES_WEBHOOK.sendMessage("", listOf(startEmbed()))

        instance.logger.info("")
    }
    override fun onEnable() {
        PlayerRegistry.register(Player.CONSOLE)
        BukkitEventBridge(EVENT_REGISTRY).registerAllEvents()

        EventScheduler.startTimedEvent(RestartEvent())
        EventScheduler.startTimedEvent(MotdEvent())
        PermissionSaveScheduler.start()

        MenuManager.initialize()
    }

    override fun onDisable() {
        // Persist permissions immediately during plugin disable to ensure no changes are lost
        PermissionShutdownHook.persistNow()

        // Also register JVM shutdown hook as a fallback
        PermissionShutdownHook.register()

        if (RESTARTING) UPDATES_WEBHOOK.sendMessage("", listOf(restartEmbed()))
        else UPDATES_WEBHOOK.sendMessage("", listOf(stopEmbed()))
    }
}
