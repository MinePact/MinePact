package net.minepact.core.global.events.timed

import net.minepact.Main
import net.minepact.api.player.PlayerRegistry
import net.minepact.api.scheduler.TimeInterval
import net.minepact.api.scheduler.TimedEvent

class VanishEvent : TimedEvent(
    interval = TimeInterval.SECOND / 2
) {
    override fun run() {
        PlayerRegistry.online().forEach { player ->
            PlayerRegistry.vanished().forEach { vanishedPlayer ->
                if (player.data.uuid == vanishedPlayer.data.uuid) return@forEach
                if (vanishedPlayer.console()) return@forEach

                player.asPlayer()?.hidePlayer(
                    Main.instance,
                    vanishedPlayer.asPlayer()!!
                )
            }
        }
    }
}