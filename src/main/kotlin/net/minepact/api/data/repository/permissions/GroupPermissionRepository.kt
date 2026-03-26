package net.minepact.api.data.repository.permissions

import net.minepact.api.data.helper.*
import net.minepact.api.data.repository.Repository
import net.minepact.api.permissions.*
import net.minepact.api.permissions.repository.GroupPermissionRow
import net.minepact.api.permissions.PermissionScope
import net.minepact.Main
import java.sql.ResultSet
import java.time.Instant
import java.util.concurrent.CompletableFuture

object GroupPermissionRepository : Repository<GroupPermissionRow>() {
    override fun table() = TableBuilder("group_permissions")
        .column("groupName", DataType.STRING, primaryKey = true)
        .column("groupServerId", DataType.STRING, primaryKey = true)
        .column("node", DataType.STRING, primaryKey = true)
        .column("value", DataType.INT, nullable = false)
        .column("expiresAt", DataType.STRING, nullable = true)
        .build()

    override fun map(rs: ResultSet) = GroupPermissionRow(
        groupName = rs.getString("groupName"),
        groupServerId = rs.getString("groupServerId"),
        node = rs.getString("node"),
        value = rs.getInt("value") == 1,
        expiresAt = rs.getString("expiresAt")
            ?.takeIf { it.isNotBlank() }
            ?.let { Instant.parse(it) }
    )

    override fun insertValues(entity: GroupPermissionRow) = listOf(
        entity.groupName,
        entity.groupServerId,
        entity.node,
        if (entity.value) 1 else 0,
        entity.expiresAt?.toString() ?: ""
    )

    private val currentId: String
        get() = Main.SERVER.info.uuid.toString()

    fun findAllForGroup(groupName: String, scope: PermissionScope = PermissionScope.ALL): CompletableFuture<List<Permission>> {
        return when (scope) {
            PermissionScope.ALL -> queryList(
                "SELECT * FROM group_permissions WHERE groupName = ? AND groupServerId IN (?, ?)",
                listOf(groupName, currentId, PermissionScope.GLOBAL.name),
                ::map
            ).thenApply { rows -> rows.map { Permission(it.node, it.value, it.expiresAt) } }

            PermissionScope.LOCAL -> queryList(
                "SELECT * FROM group_permissions WHERE groupName = ? AND groupServerId = ?",
                listOf(groupName, currentId),
                ::map
            ).thenApply { rows -> rows.map { Permission(it.node, it.value, it.expiresAt) } }

            PermissionScope.GLOBAL -> queryList(
                "SELECT * FROM group_permissions WHERE groupName = ? AND groupServerId = ?",
                listOf(groupName, PermissionScope.GLOBAL.name),
                ::map
            ).thenApply { rows -> rows.map { Permission(it.node, it.value, it.expiresAt) } }
        }
    }

    fun save(groupName: String, groupServerId: String, permission: Permission): CompletableFuture<Int> =
        insert(GroupPermissionRow(groupName, groupServerId, permission.node, permission.value, permission.expiresAt))

    fun delete(groupName: String, groupServerId: String, node: String): CompletableFuture<Int> =
        database.update(
            "DELETE FROM group_permissions WHERE groupName = ? AND groupServerId = ? AND node = ?",
            listOf(groupName, groupServerId, node)
        )

    fun deleteAllForGroup(groupName: String, groupServerId: String): CompletableFuture<Int> =
        database.update(
            "DELETE FROM group_permissions WHERE groupName = ? AND groupServerId = ?",
            listOf(groupName, groupServerId)
        )
}