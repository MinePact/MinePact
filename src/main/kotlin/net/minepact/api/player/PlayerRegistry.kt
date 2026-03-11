package net.minepact.api.player

import net.minepact.api.data.repository.PlayerPermissionStateRepository
import net.minepact.api.data.repository.PlayerRepository
import net.minepact.api.permissions.GroupRegistry
import net.minepact.api.permissions.PermissionCache
import net.minepact.api.permissions.PlayerGroupData
import net.minepact.api.permissions.PlayerPermissionData
import net.minepact.api.permissions.graph.PermissionCompiler
import net.minepact.api.world.Position
import java.util.UUID
import java.util.concurrent.CompletableFuture

object PlayerRegistry {
    val playersByUUID: MutableMap<UUID, Player> = mutableMapOf()
    private val nameToUUID: MutableMap<String, UUID> = mutableMapOf()

    fun all(): List<Player> = playersByUUID.values.toList()
    fun online(): List<Player> = playersByUUID.values.filter { it.online }
    fun vanished(): List<Player> = online().filter { it.vanished }

    fun get(uuid: UUID): CompletableFuture<Player> {
        playersByUUID[uuid]?.let {
            return CompletableFuture.completedFuture(it)
        }

        return PlayerRepository.findByUUID(uuid).thenCompose { data ->
            if (data == null) return@thenCompose CompletableFuture.completedFuture(null)

            PlayerPermissionStateRepository.find(uuid).thenApply { state ->
                val groups = state?.groups
                    ?.mapNotNull { GroupRegistry.get(it).get() }
                    ?.toMutableList()
                    ?: mutableListOf()
                val perms = state?.permissions ?: mutableSetOf()
                val player = Player(
                    data = data,
                    pos = Position.spawn(),
                    groupData = PlayerGroupData(groups),
                    permissionData = PlayerPermissionData(perms),
                    online = false,
                    vanished = false
                )

                register(player)
                PermissionCache.put(
                    player.data.uuid,
                    PermissionCompiler.compile(player)
                )
                player
            }
        }
    }
    fun get(name: String): CompletableFuture<Player> {
        val lower = name.lowercase()
        nameToUUID[lower]?.let { uuid -> playersByUUID[uuid]?.let {
                return CompletableFuture.completedFuture(it)
        } }
        return PlayerRepository.findByName(lower).thenApply { data ->
            if (data == null) return@thenApply null
            val player = Player(
                data,
                pos = Position.spawn(),
                groupData = PlayerGroupData(groups = mutableListOf( /* TODO */)),
                permissionData = PlayerPermissionData(perms = mutableSetOf( /* TODO */)),
                online = false,
                vanished = false
            )
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