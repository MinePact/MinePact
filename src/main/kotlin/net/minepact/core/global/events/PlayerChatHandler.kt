package net.minepact.core.global.events

import net.minepact.api.event.EventContext
import net.minepact.api.event.SimpleEventHandler
import org.bukkit.event.player.PlayerChatEvent

@Suppress("DEPRECATION")
class PlayerChatHandler : SimpleEventHandler<PlayerChatEvent>() {
    override fun handle(context: EventContext<PlayerChatEvent>) {
        val event = context.event
        val player = event.player
        val message = event.message

        event.format = "${player.name}: $message"
    }
}