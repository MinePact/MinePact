package net.minepact.core.global.events

import net.minepact.api.event.EventContext
import net.minepact.api.event.SimpleEventHandler
import net.minepact.api.player.PlayerRegistry
import org.bukkit.event.player.PlayerQuitEvent

class PlayerQuitHandler : SimpleEventHandler<PlayerQuitEvent>() {
    override fun handle(context: EventContext<PlayerQuitEvent>) {
        val event = context.event
        val player = event.player

        PlayerRegistry.get(player.uniqueId).get().online = false
    }
}