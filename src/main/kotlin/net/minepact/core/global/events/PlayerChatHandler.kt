package net.minepact.core.global.events

import io.papermc.paper.event.player.AsyncChatEvent
import net.minepact.api.event.EventContext
import net.minepact.api.event.SimpleEventHandler
import net.minepact.api.messages.send

class PlayerChatHandler : SimpleEventHandler<AsyncChatEvent>() {
    override fun handle(context: EventContext<AsyncChatEvent>) {

    }
}