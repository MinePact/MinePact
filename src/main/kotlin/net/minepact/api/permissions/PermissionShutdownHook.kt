package net.minepact.api.permissions

import net.minepact.api.player.PlayerRegistry

object PermissionShutdownHook {
    fun register() {
        Runtime.getRuntime().addShutdownHook(Thread {
            PlayerRegistry.all().forEach { PermissionSaveScheduler.savePlayer(it) }
        })
    }
}