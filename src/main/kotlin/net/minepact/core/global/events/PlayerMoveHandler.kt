package net.minepact.core.global.events

import net.minepact.api.event.EventContext
import net.minepact.api.event.SimpleEventHandler
import net.minepact.api.math.helper.vector.vec
import net.minepact.api.player.PlayerRegistry
import net.minepact.api.world.Position
import org.bukkit.event.player.PlayerMoveEvent

class PlayerMoveHandler : SimpleEventHandler<PlayerMoveEvent>() {
    override fun handle(context: EventContext<PlayerMoveEvent>) {
        val event = context.event
        val player = event.player

        PlayerRegistry.get(player.uniqueId).thenAccept {
            it.pos = Position(
                vector = vec(player.x, player.y, player.z),
                yaw = player.yaw,
                pitch = player.pitch,
                world = player.world.name
            )
        }
    }
}