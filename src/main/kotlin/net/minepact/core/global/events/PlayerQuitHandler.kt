package net.minepact.core.global.events

import net.minepact.api.data.repository.PlayerRepository
import net.minepact.api.event.EventContext
import net.minepact.api.event.SimpleEventHandler
import net.minepact.api.player.Player
import net.minepact.api.player.PlayerRegistry
import org.bukkit.event.player.PlayerQuitEvent

class PlayerQuitHandler : SimpleEventHandler<PlayerQuitEvent>() {
    override fun handle(context: EventContext<PlayerQuitEvent>) {
        val event = context.event

        val player: Player = PlayerRegistry.get(event.player.uniqueId).get()
        player.online = false
        player.data.lastSeen = System.currentTimeMillis()
        PlayerRepository.insert(player.data)
    }
}