package net.minepact.api.data.repository.permissions

import net.minepact.api.data.helper.DataType
import net.minepact.api.data.helper.TableBuilder
import net.minepact.api.data.repository.Repository
import net.minepact.api.permissions.repository.PlayerGroupRow
import net.minepact.api.permissions.PermissionScope
import net.minepact.Main
import java.sql.ResultSet
import java.time.Instant
import java.util.UUID
import java.util.concurrent.CompletableFuture

object PlayerGroupRepository : Repository<PlayerGroupRow>() {
    override fun table() = TableBuilder("player_groups")
        .column("uuid", DataType.UUID, primaryKey = true)
        .column("serverId", DataType.STRING, primaryKey = true)
        .column("groupName", DataType.STRING, primaryKey = true)
        .column("groupServerId", DataType.STRING, primaryKey = true)
        .column("expiresAt", DataType.STRING, nullable = true)
        .build()

    override fun map(rs: ResultSet) = PlayerGroupRow(
        uuid = UUID.fromString(rs.getString("uuid")),
        serverId = rs.getString("serverId"),
        groupName = rs.getString("groupName"),
        groupServerId = rs.getString("groupServerId"),
        expiresAt = rs.getString("expiresAt")
            ?.takeIf { it.isNotBlank() }
            ?.let { Instant.parse(it) }
    )

    override fun insertValues(entity: PlayerGroupRow) = listOf(
        entity.uuid.toString(),
        entity.serverId,
        entity.groupName,
        entity.groupServerId,
        entity.expiresAt?.toString() ?: ""
    )

    private val currentId: String
        get() = Main.SERVER.info.uuid.toString()

    fun findAll(uuid: UUID, scope: PermissionScope): CompletableFuture<List<PlayerGroupRow>> =
        when (scope) {
            PermissionScope.ALL -> queryList(
                """
                SELECT * FROM player_groups
                WHERE uuid = ? AND serverId IN (?, ?)
            """.trimIndent(),
                listOf(uuid.toString(), currentId, PermissionScope.GLOBAL.name),
                ::map
            )
            PermissionScope.LOCAL -> queryList(
                "SELECT * FROM player_groups WHERE uuid = ? AND serverId = ?",
                listOf(uuid.toString(), currentId),
                ::map
            )
            PermissionScope.GLOBAL -> queryList(
                "SELECT * FROM player_groups WHERE uuid = ? AND serverId = ?",
                listOf(uuid.toString(), PermissionScope.GLOBAL.name),
                ::map
            )
        }

    fun save(row: PlayerGroupRow): CompletableFuture<Int> = insert(row)

    fun delete(uuid: UUID, serverId: String, groupName: String, groupServerId: String): CompletableFuture<Int> =
        database.update(
            """
                DELETE FROM player_groups
                WHERE uuid = ? AND serverId = ? AND groupName = ? AND groupServerId = ?
            """.trimIndent(),
            listOf(uuid.toString(), serverId, groupName, groupServerId)
        )

    fun deleteAllForPlayer(uuid: UUID, scope: PermissionScope): CompletableFuture<Int> =
        when (scope) {
            PermissionScope.LOCAL -> database.update(
                "DELETE FROM player_groups WHERE uuid = ? AND serverId = ?",
                listOf(uuid.toString(), currentId)
            )
            PermissionScope.GLOBAL -> database.update(
                "DELETE FROM player_groups WHERE uuid = ? AND serverId = ?",
                listOf(uuid.toString(), PermissionScope.GLOBAL.name)
            )
            PermissionScope.ALL -> database.update(
                "DELETE FROM player_groups WHERE uuid = ?",
                listOf(uuid.toString())
            )
        }
}