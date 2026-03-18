package net.minepact.api.data.repository

import net.minepact.api.data.helper.DataType
import net.minepact.api.data.helper.TableBuilder
import net.minepact.api.permissions.Permission
import net.minepact.api.permissions.PlayerPermissionState
import java.sql.ResultSet
import java.util.UUID
import java.util.concurrent.CompletableFuture

object PlayerPermissionStateRepository : Repository<PlayerPermissionState>() {
    override fun table() = TableBuilder("player_permission_state")
        .column("uuid", DataType.UUID, primaryKey = true)
        .column("serverId", DataType.STRING, nullable = false)
        .column("groups", DataType.STRING, nullable = false)
        .column("permissions", DataType.STRING, nullable = false)
        .column("last_updated", DataType.LONG, nullable = false)
        .build()
    override fun map(rs: ResultSet): PlayerPermissionState {
        val uuid = UUID.fromString(rs.getString("uuid"))
        val serverId = rs.getString("serverId")
        val groups = rs.getString("groups")
            .split(";")
            .filter { it.isNotBlank() }
        val perms = rs.getString("permissions")
            .split(";")
            .filter { it.isNotBlank() }
            .map { Permission.fromString(it) }

        return PlayerPermissionState(
            uuid,
            serverId,
            groups.toMutableList(),
            perms.toMutableSet()
        )
    }
    override fun insertValues(entity: PlayerPermissionState): List<Any> {
        return listOf(
            entity.uuid.toString(),
            entity.serverId,
            entity.groups.joinToString(";"),
            entity.permissions.joinToString(";"),
            System.currentTimeMillis()
        )
    }

    fun find(uuid: UUID): CompletableFuture<PlayerPermissionState?> =
        querySingle(
            "SELECT * FROM player_permission_state WHERE uuid = ?",
            listOf(uuid.toString()),
            ::map
        )
    fun findAll(uuid: UUID, serverId: String): CompletableFuture<List<PlayerPermissionState>> {
        return queryList(
            """
                SELECT * FROM player_permission_state
                WHERE uuid = ?
                AND serverId IN ('GLOBAL', ?)
            """.trimIndent(),
            listOf(uuid.toString(), serverId),
            ::map
        )
    }
}