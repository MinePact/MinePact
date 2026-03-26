package net.minepact.api.player

import net.minepact.Main
import net.minepact.api.data.repository.permissions.PlayerPermissionStateRepository
import net.minepact.api.data.repository.PlayerRepository
import net.minepact.api.permissions.GroupRegistry
import net.minepact.api.permissions.PermissionCache
import net.minepact.api.permissions.PermissionScope
import net.minepact.api.permissions.repository.PlayerGroupData
import net.minepact.api.permissions.repository.PlayerPermissionData
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

    /* ------------------------------------- GET BY UUID ------------------------------------- */

    fun get(uuid: UUID): CompletableFuture<Player> {
        playersByUUID[uuid]?.let { return CompletableFuture.completedFuture(it) }

        return PlayerRepository.findByUUID(uuid).thenCompose { data ->
            if (data == null) return@thenCompose CompletableFuture.completedFuture(null)

            PlayerPermissionStateRepository.findAll(uuid, PermissionScope.ALL).thenApply { states ->
                    val globalState = states.firstOrNull { it.serverId == "GLOBAL" }
                    val localState = states.firstOrNull { it.serverId == Main.SERVER.info.uuid.toString() }

                    val globalGroups = globalState?.groups
                        ?.mapNotNull { GroupRegistry.get(it).join() }
                        ?.toMutableList()
                        ?: mutableListOf()

                    val localGroups = localState?.groups
                        ?.mapNotNull { GroupRegistry.get(it).join() }
                        ?.toMutableList()
                        ?: mutableListOf()

                    val globalPerms = globalState?.permissions?.toMutableSet() ?: mutableSetOf()
                    val localPerms = localState?.permissions?.toMutableSet() ?: mutableSetOf()

                    val player = Player(
                        data = data,
                        pos = Position.spawn(),

                        ipHistory = mutableListOf(),

                        globalGroupData = PlayerGroupData(globalGroups),
                        localGroupData = PlayerGroupData(localGroups),
                        globalPermissionData = PlayerPermissionData(globalPerms),
                        localPermissionData = PlayerPermissionData(localPerms),

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

    /* ------------------------------------- GET BY NAME ------------------------------------- */

    fun get(name: String): CompletableFuture<Player> {
        val lower = name.lowercase()
        nameToUUID[lower]?.let { uuid ->
            playersByUUID[uuid]?.let {
                return CompletableFuture.completedFuture(it)
            }
        }

        return PlayerRepository.findByName(lower).thenCompose { data ->
            if (data == null) return@thenCompose CompletableFuture.completedFuture(null)
            get(data.uuid)
        }
    }

    /* ------------------------------------- REGISTRY ------------------------------------- */

    fun register(player: Player) {
        playersByUUID[player.data.uuid] = player
        nameToUUID[player.data.name.lowercase()] = player.data.uuid
    }
    fun unregister(uuid: UUID) {
        val player = playersByUUID.remove(uuid) ?: return
        nameToUUID.remove(player.data.name.lowercase())
    }
}