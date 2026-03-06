package net.minepact.core.global.events

import com.destroystokyo.paper.event.player.PlayerJumpEvent
import net.minepact.api.config.experimental.ConfigurationRegistry
import net.minepact.core.global.configs.PluginConfig
import net.minepact.api.event.EventContext
import net.minepact.api.event.SimpleEventHandler

class PlayerJumpHandler : SimpleEventHandler<PlayerJumpEvent>() {
    override fun handle(context: EventContext<PlayerJumpEvent>) {
        val event: PlayerJumpEvent = context.event
        if (!ConfigurationRegistry.get(PluginConfig::class).jumpBoostPlayers.contains(event.player.name)) {
            return
        }

        if (event.player.isSneaking) {
            val velocity = event.player.velocity
            velocity.y = ConfigurationRegistry.get(PluginConfig::class).jumpBoostVelocity
            event.player.velocity = velocity
        }
    }
}