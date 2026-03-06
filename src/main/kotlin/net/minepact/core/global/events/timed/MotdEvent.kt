package net.minepact.core.global.events.timed

import net.minepact.Main
import net.minepact.api.config.experimental.ConfigurationRegistry
import net.minepact.api.messages.FormatParser
import net.minepact.api.schedular.TimeInterval
import net.minepact.api.schedular.TimedEvent
import net.minepact.core.global.configs.MotdConfig
import org.bukkit.Bukkit

class MotdEvent : TimedEvent(
    interval = TimeInterval.SECOND * 5
) {
    override fun run() {
        val config: MotdConfig = ConfigurationRegistry.get(MotdConfig::class)

        if (Main.SERVER.maintenanceMode()) Bukkit.getServer().motd(FormatParser.parse(config.maintenance))
        else Bukkit.getServer().motd(FormatParser.parse(config.motd))
    }
}