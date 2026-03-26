package net.minepact.api.data.repository.permissions

import net.minepact.api.data.helper.DataType
import net.minepact.api.data.helper.TableBuilder
import net.minepact.api.data.repository.Repository
import net.minepact.api.permissions.Permission
import net.minepact.api.permissions.repository.PlayerPermissionRow
import net.minepact.api.permissions.PermissionScope
import net.minepact.Main
import java.sql.ResultSet
import java.time.Instant
import java.util.UUID
import java.util.concurrent.CompletableFuture

object PlayerPermissionRepository : Repository<PlayerPermissionRow>() {
    override fun table() = TableBuilder("player_permissions")
        .column("uuid", DataType.UUID, primaryKey = true)
        .column("serverId", DataType.STRING, primaryKey = true)
        .column("node", DataType.STRING, primaryKey = true)
        .column("value", DataType.INT, nullable = false)
        .column("expiresAt", DataType.STRING, nullable = true)
        .build()

    override fun map(rs: ResultSet) = PlayerPermissionRow(
        uuid = UUID.fromString(rs.getString("uuid")),
        serverId = rs.getString("serverId"),
        node = rs.getString("node"),
        value = rs.getInt("value") == 1,
        expiresAt = rs.getString("expiresAt")
            ?.takeIf { it.isNotBlank() }
            ?.let { Instant.parse(it) }
    )

    override fun insertValues(entity: PlayerPermissionRow) = listOf(
        entity.uuid.toString(),
        entity.serverId,
        entity.node,
        if (entity.value) 1 else 0,
        entity.expiresAt?.toString() ?: ""
    )

    private val currentId: String
        get() = Main.SERVER.info.uuid.toString()

    fun findAll(uuid: UUID, scope: PermissionScope = PermissionScope.ALL): CompletableFuture<Set<Permission>> {
        return when (scope) {
            PermissionScope.ALL -> queryList(
                """
                SELECT * FROM player_permissions
                WHERE uuid = ? AND serverId IN (?, ?)
                ORDER BY CASE WHEN serverId = ? THEN 1 ELSE 0 END
            """.trimIndent(),
                listOf(uuid.toString(), currentId, PermissionScope.GLOBAL.name, currentId),
                ::map
            ).thenApply { rows ->
                rows.associateBy { it.node }
                    .values
                    .map { Permission(it.node, it.value, it.expiresAt) }
                    .toSet()
            }
            PermissionScope.LOCAL -> queryList(
                "SELECT * FROM player_permissions WHERE uuid = ? AND serverId = ?",
                listOf(uuid.toString(), currentId),
                ::map
            ).thenApply { rows ->
                rows.associateBy { it.node }
                    .values
                    .map { Permission(it.node, it.value, it.expiresAt) }
                    .toSet()
            }
            PermissionScope.GLOBAL -> queryList(
                "SELECT * FROM player_permissions WHERE uuid = ? AND serverId = ?",
                listOf(uuid.toString(), PermissionScope.GLOBAL.name),
                ::map
            ).thenApply { rows ->
                rows.associateBy { it.node }
                    .values
                    .map { Permission(it.node, it.value, it.expiresAt) }
                    .toSet()
            }
        }
    }

    fun save(uuid: UUID, serverId: String, permission: Permission): CompletableFuture<Int> =
        insert(PlayerPermissionRow(uuid, serverId, permission.node, permission.value, permission.expiresAt))

    fun delete(uuid: UUID, serverId: String, node: String): CompletableFuture<Int> =
        database.update(
            "DELETE FROM player_permissions WHERE uuid = ? AND serverId = ? AND node = ?",
            listOf(uuid.toString(), serverId, node)
        )

    fun deleteAllForPlayer(uuid: UUID, serverId: String): CompletableFuture<Int> =
        database.update(
            "DELETE FROM player_permissions WHERE uuid = ? AND serverId = ?",
            listOf(uuid.toString(), serverId)
        )
}