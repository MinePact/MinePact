package net.minepact

import net.minepact.api.command.CommandRegister
import net.minepact.api.config.ConfigurationRegistry
import net.minepact.api.discord.Webhook
import net.minepact.api.discord.embed.Author
import net.minepact.api.discord.embed.Embed
import net.minepact.api.discord.embed.Field
import net.minepact.api.discord.embed.Footer
import net.minepact.api.event.BukkitEventBridge
import net.minepact.api.event.EventRegister
import net.minepact.api.reflections.findCommands
import net.minepact.api.reflections.findEvents
import net.minepact.api.reflections.registerConfigs
import net.minepact.api.server.Server
import net.minepact.core.global.configs.ServerConfig
import net.minepact.core.global.configs.PluginConfig
import org.bukkit.plugin.java.JavaPlugin
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.properties.Delegates

class Main : JavaPlugin() {
    companion object {
        lateinit var instance: Main
        lateinit var COMMAND_REGISTRY: CommandRegister
        lateinit var EVENT_REGISTRY: EventRegister

        lateinit var MAIN_CONFIG: PluginConfig
        lateinit var SERVER: Server

        var SERVER_START_TIME by Delegates.notNull<Long>()
    }

    override fun onLoad() {
        instance = this
        COMMAND_REGISTRY = CommandRegister()
        EVENT_REGISTRY = EventRegister()
        SERVER_START_TIME = System.currentTimeMillis()

        ConfigurationRegistry.register(ServerConfig::class)
        SERVER = Server()

        findCommands("net.minepact.core").forEach { COMMAND_REGISTRY.register(it) }
        findEvents("net.minepact.core").forEach { EVENT_REGISTRY.register(it) }

        registerConfigs("net.minepact.core.global.configs")
        MAIN_CONFIG = ConfigurationRegistry.get(PluginConfig::class)

        val FORMATTED_START_TIME = SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Date(SERVER_START_TIME))

        Webhook("Server Updates", URL("https://www.balls.com/suck/my/balls"))
            .sendMessage("", listOf(Embed(
                author = Author(),
                title = ":green_circle: Server Started",
                url = null,
                description = "${SERVER.name} has started!",
                colour = 0x00FF00,
                fields = listOf(
                    Field("Name", SERVER.name, true),
                    Field("Type", SERVER.type.name, true),
                    Field("Staging", SERVER.staging.toString(), true)
                ),
                thumbnail = null,
                image = null,
                footer = Footer("Server UUID: ${SERVER.uuid} | Time: $FORMATTED_START_TIME")
            )
        ))
    }

    override fun onEnable() {
        BukkitEventBridge(EVENT_REGISTRY).registerAllEvents()
    }

    override fun onDisable() {
        Webhook("Server Updates", URL("https://www.balls.com/suck/my/balls"))
            .sendMessage("", listOf(Embed(
                author = Author(),
                title = ":red_circle: Server Stopped",
                url = null,
                description = "${SERVER.name} has stopped!",
                colour = 0xFF0000,
                fields = listOf(
                    Field("Name", SERVER.name, true),
                    Field("Type", SERVER.type.name, true),
                    Field("Staging", SERVER.staging.toString(), true)
                ),
                thumbnail = null,
                image = null,
                footer = Footer("Server UUID: ${SERVER.uuid} | Time: ${SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Date(System.currentTimeMillis()))}")
            )
        ))
    }
}
