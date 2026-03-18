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
        val globalState = PlayerPermissionState(
            uuid = player.data.uuid,
            serverId = "GLOBAL",
            groups = player.globalGroupData.groups.map { it.name }.toMutableList(),
            permissions = player.globalPermissionData.perms
        )
        val localState = PlayerPermissionState(
            uuid = player.data.uuid,
            serverId = Main.SERVER.info.uuid.toString(),
            groups = player.localGroupData.groups.map { it.name }.toMutableList(),
            permissions = player.localPermissionData.perms
        )

        PlayerPermissionStateRepository.insert(globalState)
        PlayerPermissionStateRepository.insert(localState)

        PermissionPersistence.clear(player.data.uuid)
    }
}