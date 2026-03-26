package net.minepact.api.data.repository.permissions

import net.minepact.api.data.helper.DataType
import net.minepact.api.data.helper.TableBuilder
import net.minepact.api.data.repository.Repository
import net.minepact.api.permissions.Group
import net.minepact.api.permissions.PermissionScope
import net.minepact.Main
import java.sql.ResultSet
import java.time.Instant
import java.util.concurrent.CompletableFuture

object GroupRepository : Repository<Group>() {
    override fun table() = TableBuilder("groups")
        .column("name", DataType.STRING, primaryKey = true)
        .column("serverId", DataType.STRING, primaryKey = true)
        .column("displayName", DataType.STRING, nullable = true)
        .column("weight", DataType.INT, nullable = false)
        .column("prefix", DataType.STRING, nullable = true)
        .column("suffix", DataType.STRING, nullable = true)
        .column("expiresAt", DataType.STRING, nullable = true)
        .build()
    override fun map(rs: ResultSet) = Group(
        name = rs.getString("name"),
        serverId = rs.getString("serverId"),
        displayName = rs.getString("displayName"),
        weight = rs.getInt("weight"),
        prefix = rs.getString("prefix"),
        suffix = rs.getString("suffix"),
        expiresAt = rs.getString("expiresAt")
            ?.takeIf { it.isNotBlank() }
            ?.let { Instant.parse(it) }
    )
    override fun insertValues(entity: Group) = listOf(
        entity.name,
        entity.serverId,
        entity.displayName ?: "",
        entity.weight,
        entity.prefix ?: "",
        entity.suffix ?: "",
        entity.expiresAt?.toString() ?: ""
    )

    private val currentId: String
        get() = Main.SERVER.info.uuid.toString()

    fun findByName(name: String, scope: PermissionScope = PermissionScope.ALL): CompletableFuture<Group?> {
        return when (scope) {
            PermissionScope.ALL -> queryList(
                """
                SELECT * FROM groups
                WHERE name = ? AND serverId IN (?, ?)
                ORDER BY CASE WHEN serverId = ? THEN 1 ELSE 0 END
            """.trimIndent(),
                listOf(name, currentId, PermissionScope.GLOBAL.name, currentId),
                ::map
            ).thenApply { it.firstOrNull() }

            PermissionScope.LOCAL -> querySingle(
                "SELECT * FROM groups WHERE name = ? AND serverId = ?",
                listOf(name, currentId),
                ::map
            )

            PermissionScope.GLOBAL -> querySingle(
                "SELECT * FROM groups WHERE name = ? AND serverId = ?",
                listOf(name, PermissionScope.GLOBAL.name),
                ::map
            )
        }
    }
    fun findAll(scope: PermissionScope = PermissionScope.ALL): CompletableFuture<List<Group>> {
        return when (scope) {
            PermissionScope.ALL -> queryList(
                "SELECT * FROM groups WHERE serverId IN (?, ?)",
                listOf(currentId, PermissionScope.GLOBAL.name),
                ::map
            )
            PermissionScope.LOCAL -> queryList(
                "SELECT * FROM groups WHERE serverId = ?",
                listOf(currentId),
                ::map
            )
            PermissionScope.GLOBAL -> queryList(
                "SELECT * FROM groups WHERE serverId = ?",
                listOf(PermissionScope.GLOBAL.name),
                ::map
            )
        }
    }
    fun delete(name: String, scope: PermissionScope = PermissionScope.LOCAL): CompletableFuture<Int> {
        return when (scope) {
            PermissionScope.LOCAL -> database.update(
                "DELETE FROM groups WHERE name = ? AND serverId = ?",
                listOf(name, currentId)
            )
            PermissionScope.GLOBAL -> database.update(
                "DELETE FROM groups WHERE name = ? AND serverId = ?",
                listOf(name, PermissionScope.GLOBAL.name)
            )
            PermissionScope.ALL -> database.update(
                "DELETE FROM groups WHERE name = ?",
                listOf(name)
            )
        }
    }
}