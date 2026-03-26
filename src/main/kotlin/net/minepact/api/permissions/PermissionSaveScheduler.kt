package net.minepact.api.permissions

import net.minepact.Main
import net.minepact.api.data.repository.permissions.PlayerPermissionStateRepository
import net.minepact.api.data.repository.permissions.PlayerPermissionRepository
import net.minepact.api.data.repository.permissions.PlayerGroupRepository
import net.minepact.api.permissions.repository.PlayerPermissionState
import net.minepact.api.permissions.repository.PlayerGroupRow
import net.minepact.api.player.Player
import net.minepact.api.player.PlayerRegistry
import org.bukkit.Bukkit

object PermissionSaveScheduler {
    fun start() {
        // run every minute (20 ticks * 60)
        Bukkit.getScheduler().runTaskTimerAsynchronously(
            Main.instance,
            Runnable { flushDirty() },
            20L * 60,
            20L * 60
        )
    }

    private fun flushDirty() {
        PlayerRegistry.all().forEach { player ->
            if (!PermissionPersistence.isDirty(player.data.uuid)) return@forEach
            savePlayer(player)
        }
    }

    // public helper to flush all dirty players immediately (used by shutdown)
    fun flushAllDirtyNow() {
        PlayerRegistry.all().forEach { player ->
            if (!PermissionPersistence.isDirty(player.data.uuid)) return@forEach
            savePlayer(player)
        }
    }

    fun savePlayer(player: Player) {
        val globalState = PlayerPermissionState(
            uuid = player.data.uuid,
            serverId = PermissionScope.GLOBAL.name,
            groups = player.globalGroupData.groups.map { it.name }.toMutableList(),
            permissions = player.globalPermissionData.perms
        )
        val localState = PlayerPermissionState(
            uuid = player.data.uuid,
            serverId = Main.SERVER.info.uuid.toString(),
            groups = player.localGroupData.groups.map { it.name }.toMutableList(),
            permissions = player.localPermissionData.perms
        )

        try {
            // ensure aggregated state inserts complete before returning so shutdown persistence is reliable
            PlayerPermissionStateRepository.insert(globalState).get()
            PlayerPermissionStateRepository.insert(localState).get()

            // persist full permission rows for each scope (GLOBAL and LOCAL)
            val scopes = listOf(PermissionScope.GLOBAL, PermissionScope.LOCAL)
            scopes.forEach { scope ->
                val serverId = if (scope == PermissionScope.GLOBAL) PermissionScope.GLOBAL.name else Main.SERVER.info.uuid.toString()

                // remove current rows for this player/scope then re-insert current state
                PlayerPermissionRepository.deleteAllForPlayer(player.data.uuid, serverId).get()
                val perms = when (scope) {
                    PermissionScope.GLOBAL -> player.globalPermissionData.perms
                    else -> player.localPermissionData.perms
                }
                perms.forEach { perm -> PlayerPermissionRepository.save(player.data.uuid, serverId, perm).get() }

                PlayerGroupRepository.deleteAllForPlayer(player.data.uuid, scope).get()
                val groups = when (scope) {
                    PermissionScope.GLOBAL -> player.globalGroupData.groups
                    else -> player.localGroupData.groups
                }
                groups.forEach { group ->
                    val row = PlayerGroupRow(
                        uuid = player.data.uuid,
                        serverId = serverId,
                        groupName = group.name,
                        groupServerId = group.serverId,
                        expiresAt = null
                    )
                    PlayerGroupRepository.save(row).get()
                }
            }

        } catch (t: Throwable) {
            Main.instance.logger.severe("[PermissionSaveScheduler] Failed to persist player ${player.data.uuid}: ${t.message}")
        }

        PermissionPersistence.clear(player.data.uuid)
    }
}