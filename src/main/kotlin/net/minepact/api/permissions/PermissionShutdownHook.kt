package net.minepact.api.permissions

import net.minepact.Main
import net.minepact.api.data.repository.permissions.GroupPermissionRepository
import net.minepact.api.data.repository.permissions.GroupParentRepository
import net.minepact.api.data.repository.permissions.GroupRepository
import net.minepact.api.data.repository.permissions.PlayerGroupRepository
import net.minepact.api.data.repository.permissions.PlayerPermissionRepository
import net.minepact.api.data.repository.permissions.PlayerPermissionStateRepository
import net.minepact.api.permissions.repository.GroupParentRow
import net.minepact.api.permissions.repository.PlayerGroupRow
import net.minepact.api.permissions.repository.PlayerPermissionRow
import net.minepact.api.player.PlayerRegistry
import java.util.concurrent.CompletableFuture

object PermissionShutdownHook {
    fun register() {
        Runtime.getRuntime().addShutdownHook(Thread { persistNow() })
    }

    fun persistNow() {
        try {
            Main.instance.logger.info("[PermissionShutdown] Persisting permission state before shutdown...")

            GroupRegistry.all().forEach { group ->
                try {
                    GroupRepository.insert(group).get()

                    GroupPermissionRepository.deleteAllForGroup(group.name, group.serverId).get()
                    group.permissions.forEach { perm ->
                        GroupPermissionRepository.save(group.name, group.serverId, perm).get()
                    }

                    GroupParentRepository.deleteAllForGroup(group.name, group.serverId).get()
                    group.parents.forEach { parentName ->
                        GroupParentRepository.insert(GroupParentRow(group.name, group.serverId, parentName)).get()
                    }
                } catch (t: Throwable) {
                    Main.instance.logger.severe("[PermissionShutdown] Failed to persist group ${group.name}: ${t.message}")
                }
            }

            PlayerRegistry.all().forEach { player ->
                try {
                    // persist aggregated state rows first
                    PermissionSaveScheduler.savePlayer(player)

                    val scopes = listOf(PermissionScope.GLOBAL, PermissionScope.LOCAL)
                    scopes.forEach { scope ->
                        val serverId = if (scope == PermissionScope.GLOBAL) PermissionScope.GLOBAL.name else Main.SERVER.info.uuid.toString()

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
                    Main.instance.logger.severe("[PermissionShutdown] Failed to persist player ${player.data.uuid}: ${t.message}")
                }
            }

            Main.instance.logger.info("[PermissionShutdown] Permission state persisted.")
        } catch (e: Throwable) {
            Main.instance.logger.severe("[PermissionShutdown] Unexpected error during permission persistence: ${e.message}")
        }
    }
}