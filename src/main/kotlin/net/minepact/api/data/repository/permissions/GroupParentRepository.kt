package net.minepact.api.data.repository.permissions

import net.minepact.api.data.helper.DataType
import net.minepact.api.data.helper.TableBuilder
import net.minepact.api.data.repository.Repository
import net.minepact.api.permissions.repository.GroupParentRow
import java.sql.ResultSet
import java.util.concurrent.CompletableFuture

object GroupParentRepository : Repository<GroupParentRow>() {
    override fun table() = TableBuilder("group_parents")
        .column("groupName", DataType.STRING, primaryKey = true)
        .column("groupServerId", DataType.STRING, primaryKey = true)
        .column("parentName", DataType.STRING, primaryKey = true)
        .build()

    override fun map(rs: ResultSet) = GroupParentRow(
        groupName = rs.getString("groupName"),
        groupServerId = rs.getString("groupServerId"),
        parentName = rs.getString("parentName")
    )

    override fun insertValues(entity: GroupParentRow) = listOf(
        entity.groupName,
        entity.groupServerId,
        entity.parentName
    )

    fun deleteAllForGroup(groupName: String, groupServerId: String): CompletableFuture<Int> =
        database.update(
            "DELETE FROM group_parents WHERE groupName = ? AND groupServerId = ?",
            listOf(groupName, groupServerId)
        )
}

