package net.minepact

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import net.luckperms.api.LuckPerms
import net.minepact.api.command.CommandRegister
import net.minepact.api.config.experimental.ConfigurationRegistry
import net.minepact.api.data.Database
import net.minepact.api.data.DatabaseConfig
import net.minepact.api.data.DatabaseFactory
import net.minepact.api.data.DatabaseProvider
import net.minepact.api.discord.Webhook
import net.minepact.api.event.BukkitEventBridge
import net.minepact.api.event.EventRegister
import net.minepact.api.item.enchantments.EnchantmentRegistry
import net.minepact.api.menu.MenuManager
import net.minepact.api.misc.Constants
import net.minepact.api.player.Player
import net.minepact.api.player.PlayerRegistry
import net.minepact.api.reflections.findCommands
import net.minepact.api.reflections.findEvents
import net.minepact.api.reflections.findRepositories
import net.minepact.api.reflections.registerConfigs
import net.minepact.api.schedular.EventScheduler
import net.minepact.api.scripts.ScriptManager
import net.minepact.api.server.Server
import net.minepact.core.discord.embeds.restartEmbed
import net.minepact.core.discord.embeds.startEmbed
import net.minepact.core.discord.embeds.stopEmbed
import net.minepact.core.global.configs.PluginConfig
import net.minepact.core.global.configs.ServerConfig
import net.minepact.core.global.enchantments.PickaxeEnchantment
import net.minepact.core.global.events.timed.MotdEvent
import net.minepact.core.global.events.timed.RestartEvent
import org.bukkit.Bukkit
import java.util.concurrent.Executor
import kotlin.properties.Delegates

class Main : org.bukkit.plugin.java.JavaPlugin(), CoroutineScope {
    companion object {
        lateinit var instance: Main
        lateinit var COMMAND_REGISTRY: CommandRegister
        lateinit var EVENT_REGISTRY: EventRegister

        lateinit var MAIN_CONFIG: PluginConfig
        lateinit var DATABASE_CONFIG: DatabaseConfig
        lateinit var SERVER: Server
        lateinit var DATABASE: Database

        lateinit var UPDATES_WEBHOOK: Webhook
        lateinit var LOGGING_WEBHOOK: Webhook
        lateinit var PUNISHMENTS_WEBHOOK: Webhook

        lateinit var SCRIPT_MANAGER: ScriptManager

        lateinit var LUCKPERMS_API: LuckPerms

        var RESTARTING: Boolean = false
        var SERVER_START_TIME by Delegates.notNull<Long>()
    }

    override fun onLoad() {
        instance = this
        COMMAND_REGISTRY = CommandRegister()
        EVENT_REGISTRY = EventRegister()
        SERVER_START_TIME = System.currentTimeMillis()

        ConfigurationRegistry.register(PluginConfig::class)
        ConfigurationRegistry.register(ServerConfig::class)
        MAIN_CONFIG = ConfigurationRegistry.get(PluginConfig::class)

        DATABASE_CONFIG = DatabaseConfig(
            type = DatabaseProvider.valueOf(MAIN_CONFIG.database.provider),
            host = MAIN_CONFIG.database.host,
            port = MAIN_CONFIG.database.port,
            database = MAIN_CONFIG.database.name,
            username = MAIN_CONFIG.database.username,
            password = MAIN_CONFIG.database.password,
            filePath = "mpdb.sqlite" // Fallback
        )
        DATABASE = DatabaseFactory.create(DATABASE_CONFIG)
        findRepositories("net.minepact.api.data.repository").forEach { it.ensureTableExists() }

        SERVER = Server()

        UPDATES_WEBHOOK = Webhook(username = "Server Updates", avatarUrl = Constants.WEBHOOK_AVATAR_URL)
        LOGGING_WEBHOOK = Webhook(
            username = "Logger",
            webhookUrl = "https://discord.com/api/webhooks/1472953480300990475/uWLQTkM7vykCRfUKAT1NYogeNLMFswZdEruu5mg-L3lskiLcroBMo_uqLo2xRPahGe65",
            avatarUrl = Constants.WEBHOOK_AVATAR_URL
        )
        PUNISHMENTS_WEBHOOK = Webhook(
            username = "Punishments",
            webhookUrl = "https://discord.com/api/webhooks/1473475316713525283/eXnVM7gHRbw75g_5XtDVcWRqjXqr09uA2fazva72FpiG15kPIo0ZwPAaM6Ip4Isw4NXu",
            avatarUrl = Constants.WEBHOOK_AVATAR_URL
        )

        findCommands("net.minepact.core").forEach { COMMAND_REGISTRY.register(it) }
        findEvents("net.minepact").forEach { EVENT_REGISTRY.register(it) }
        registerConfigs("net.minepact.core.global.configs")

        for (e in PickaxeEnchantment.entries) {
            EnchantmentRegistry.register(e)
        }

        SCRIPT_MANAGER = ScriptManager()
        async { SCRIPT_MANAGER.loadAll() }

        UPDATES_WEBHOOK.sendMessage("", listOf(startEmbed()))

        instance.logger.info("")
    }
    override fun onEnable() {
        val pluginManager = Bukkit.getPluginManager()
        pluginManager.plugins.forEach { println(it.name)}

        val provider = server.servicesManager.getRegistration(LuckPerms::class.java)

        if (provider == null) {
            logger.severe("LuckPerms API not found!")
            server.pluginManager.disablePlugin(this)
            return
        }

        LUCKPERMS_API = provider.provider

        PlayerRegistry.register(Player.CONSOLE)
        BukkitEventBridge(EVENT_REGISTRY).registerAllEvents()

        EventScheduler.startTimedEvent(RestartEvent())
        EventScheduler.startTimedEvent(MotdEvent())

        MenuManager.initialize()
    }
    override fun onDisable() {
        if (RESTARTING) UPDATES_WEBHOOK.sendMessage("", listOf(restartEmbed()))
        else UPDATES_WEBHOOK.sendMessage("", listOf(stopEmbed()))
    }

    private val supervisorJob = SupervisorJob()
    override val coroutineContext = supervisorJob + Dispatchers.Default

    val mainThreadExecutor: Executor = Executor { task -> server.scheduler.runTask(this, task) }
    fun <T> async(block: suspend CoroutineScope.() -> T): Deferred<T> = async(coroutineContext, block = block)
}
