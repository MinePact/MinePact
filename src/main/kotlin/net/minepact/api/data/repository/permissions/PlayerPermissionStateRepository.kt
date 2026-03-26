package net.minepact.api.data.repository.permissions

import net.minepact.Main
import net.minepact.api.data.helper.DataType
import net.minepact.api.data.helper.TableBuilder
import net.minepact.api.data.repository.Repository
import net.minepact.api.permissions.PermissionScope
import net.minepact.api.permissions.repository.PlayerPermissionState
import java.sql.ResultSet
import java.util.UUID
import java.util.concurrent.CompletableFuture

object PlayerPermissionStateRepository : Repository<PlayerPermissionState>() {
    override fun table() = TableBuilder("player_permission_state")
        .column("uuid", DataType.UUID, primaryKey = true)
        .column("serverId", DataType.STRING, primaryKey = true)
        .column("last_updated", DataType.LONG, nullable = false)
        .build()

    override fun map(rs: ResultSet) = PlayerPermissionState(
        uuid = UUID.fromString(rs.getString("uuid")),
        serverId = rs.getString("serverId"),
        groups = mutableListOf(),
        permissions = mutableSetOf()
    )

    override fun insertValues(entity: PlayerPermissionState) = listOf(
        entity.uuid.toString(),
        entity.serverId,
        System.currentTimeMillis()
    )

    fun find(uuid: UUID, scope: PermissionScope): CompletableFuture<PlayerPermissionState?> {
        val currentId = Main.SERVER.info.uuid.toString()

        return when (scope) {
            PermissionScope.LOCAL -> querySingle(
                "SELECT * FROM player_permission_state WHERE uuid = ? AND serverId = ?",
                listOf(uuid.toString(), currentId),
                ::map
            )
            PermissionScope.GLOBAL -> querySingle(
                "SELECT * FROM player_permission_state WHERE uuid = ? AND serverId = ?",
                listOf(uuid.toString(), PermissionScope.GLOBAL.name),
                ::map
            )
            PermissionScope.ALL -> querySingle(
                "SELECT * FROM player_permission_state WHERE uuid = ? AND serverId IN (?, ?)",
                listOf(uuid.toString(), currentId, PermissionScope.GLOBAL.name),
                ::map
            )
        }
    }

    fun findAll(uuid: UUID, scope: PermissionScope): CompletableFuture<List<PlayerPermissionState>> {
        val currentId = Main.SERVER.info.uuid.toString()

        return when (scope) {
            PermissionScope.LOCAL -> queryList(
                "SELECT * FROM player_permission_state WHERE uuid = ? AND serverId = ?",
                listOf(uuid.toString(), currentId),
                ::map
            )
            PermissionScope.GLOBAL -> queryList(
                "SELECT * FROM player_permission_state WHERE uuid = ? AND serverId = ?",
                listOf(uuid.toString(), PermissionScope.GLOBAL.name),
                ::map
            )
            PermissionScope.ALL -> queryList(
                "SELECT * FROM player_permission_state WHERE uuid = ? AND serverId IN (?, ?)",
                listOf(uuid.toString(), currentId, PermissionScope.GLOBAL.name),
                ::map
            )
        }
    }
}