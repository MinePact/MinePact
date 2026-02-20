package net.minepact.api.player

import net.minepact.api.data.repository.PlayerRepository
import java.util.UUID
import java.util.concurrent.CompletableFuture

object PlayerRegistry {
    val playersByUUID: MutableMap<UUID, Player> = mutableMapOf()
    private val nameToUUID: MutableMap<String, UUID> = mutableMapOf()

    fun all(): List<Player> = playersByUUID.values.toList()
    fun online(): List<Player> = playersByUUID.values.filter { it.online }

    fun get(uuid: UUID): CompletableFuture<Player> {
        playersByUUID[uuid]?.let { return CompletableFuture.completedFuture(it) }

        return PlayerRepository.findByUUID(uuid).thenApply { data ->
            if (data == null) return@thenApply null
            val player = Player(data, online = false)
            register(player)
            player
        }
    }
    fun get(name: String): CompletableFuture<Player> {
        val lower = name.lowercase()
        nameToUUID[lower]?.let { uuid ->
            playersByUUID[uuid]?.let {
                return CompletableFuture.completedFuture(it)
            }
        }
        return PlayerRepository.findByName(lower).thenApply { data ->
            if (data == null) return@thenApply null
            val player = Player(data, online = false)
            register(player)
            player
        }
    }

    fun register(player: Player) {
        playersByUUID[player.data.uuid] = player
        nameToUUID[player.data.name.lowercase()] = player.data.uuid
    }
    fun unregister(uuid: UUID) {
        val player = playersByUUID.remove(uuid) ?: return
        nameToUUID.remove(player.data.name.lowercase())
    }
}