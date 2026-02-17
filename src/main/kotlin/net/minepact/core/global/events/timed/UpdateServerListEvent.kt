package net.minepact.core.global.events.timed

import net.minepact.api.messages.send
import net.minepact.api.schedular.TimeInterval
import net.minepact.api.schedular.TimedEvent
import org.bukkit.Bukkit

class UpdateServerListEvent : TimedEvent(
    startTime = "00:00:00",
    interval = TimeInterval.SECOND / 2
) {
    override fun run() {
        Bukkit.getOnlinePlayers().forEach { p -> p.send("a") }
    }
}