package net.minepact.api.permissions

import net.minepact.Main
import net.minepact.api.data.repository.PlayerPermissionStateRepository
import net.minepact.api.player.Player
import net.minepact.api.player.PlayerRegistry
import org.bukkit.Bukkit

object PermissionSaveScheduler {

    fun start() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(
            Main.instance,
            Runnable { flushDirty() },
            20L * 300,
            20L * 300
        )

    }

    private fun flushDirty() {
        PlayerRegistry.all().forEach { player ->
            if (!PermissionPersistence.isDirty(player.data.uuid)) return@forEach
            savePlayer(player)
        }
    }
    fun savePlayer(player: Player) {
        val state = PlayerPermissionState(
            uuid = player.data.uuid,
            groups = player.groupData.groups.map { it.name }.toMutableList(),
            permissions = player.permissionData.perms
        )

        PlayerPermissionStateRepository.insert(state)
        PermissionPersistence.clear(player.data.uuid)
    }
}