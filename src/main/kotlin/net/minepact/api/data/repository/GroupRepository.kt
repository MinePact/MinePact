package net.minepact.api.data.repository

import net.minepact.api.constants.PERMISSION_SEPARATOR
import net.minepact.api.data.helper.DataType
import net.minepact.api.data.helper.TableBuilder
import net.minepact.api.permissions.Group
import net.minepact.api.permissions.Permission
import java.sql.ResultSet
import java.time.Instant
import java.util.concurrent.CompletableFuture

object GroupRepository : Repository<Group>() {
    override fun table() = TableBuilder("groups")
        .column("name", DataType.STRING, primaryKey = true)
        .column("displayName", DataType.STRING, nullable = true)
        .column("weight", DataType.INT, nullable = false)
        .column("prefix", DataType.STRING, nullable = true)
        .column("suffix", DataType.STRING, nullable = true)
        .column("permissions", DataType.STRING, nullable = false)
        .column("parents", DataType.STRING, nullable = false)
        .column("expiresAt", DataType.STRING, nullable = true)
        .build()
    override fun map(rs: ResultSet): Group = Group(
            name = rs.getString("name"),
            displayName = rs.getString("displayName"),
            weight = rs.getInt("weight"),
            prefix = rs.getString("prefix"),
            suffix = rs.getString("suffix"),
            permissions = rs.getString("permissions")
                .takeIf { it.isNotBlank() }
                ?.split(PERMISSION_SEPARATOR)
                ?.map { Permission.fromString(it) }
                ?.toMutableSet()
                ?: mutableSetOf(),
            parents = rs.getString("parents")
                .takeIf { it.isNotBlank() }
                ?.split(",")
                ?.toMutableSet()
                ?: mutableSetOf(),
            expiresAt = rs.getString("expiresAt")
                ?.takeIf { it.isNotBlank() }
                ?.let { Instant.parse(it) }
        )
    override fun insertValues(entity: Group): List<Any> = listOf(
            entity.name,
            entity.displayName ?: "",
            entity.weight,
            entity.prefix ?: "",
            entity.suffix ?: "",
            entity.permissions.joinToString("$PERMISSION_SEPARATOR"),
            entity.parents.joinToString(","),
            entity.expiresAt?.toString() ?: ""
        )

    fun findByName(name: String): CompletableFuture<Group?> =
        querySingle(
            "SELECT * FROM groups WHERE name = ?",
            listOf(name),
            ::map
        )
    fun findByDisplayName(displayName: String): CompletableFuture<Group?> =
        querySingle(
            "SELECT * FROM groups WHERE displayName = ?",
            listOf(displayName),
            ::map
        )
    fun findByWeight(weight: Int): CompletableFuture<Group?> =
        querySingle(
            "SELECT * FROM groups WHERE weight = ?",
            listOf(weight),
            ::map
        )
    fun findByPrefix(prefix: String): CompletableFuture<Group?> =
        querySingle(
            "SELECT * FROM groups WHERE prefix = ?",
            listOf(prefix),
            ::map
        )
    fun findBySuffix(suffix: String): CompletableFuture<Group?> =
        querySingle(
            "SELECT * FROM groups WHERE suffix = ?",
            listOf(suffix),
            ::map
        )

    fun delete(group: Group) {
        querySingle("DELETE FROM groups WHERE name = ?", listOf(group.name)) { null }
    }
}