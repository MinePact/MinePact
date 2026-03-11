package net.minepact.api.permissions

import net.minepact.api.player.Player
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object PermissionPersistence {
    private val dirtyPlayers = ConcurrentHashMap.newKeySet<UUID>()

    fun markDirty(player: Player) {
        dirtyPlayers.add(player.data.uuid)
    }
    fun isDirty(uuid: UUID): Boolean = dirtyPlayers.contains(uuid)
    fun clear(uuid: UUID) {
        dirtyPlayers.remove(uuid)
    }
}