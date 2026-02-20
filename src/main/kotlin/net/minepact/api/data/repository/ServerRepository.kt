package net.minepact.api.data.repository

import net.minepact.api.data.helper.DataType
import net.minepact.api.data.helper.TableBuilder
import net.minepact.api.server.ServerInfo
import net.minepact.api.server.ServerType
import java.sql.ResultSet
import java.util.UUID
import java.util.concurrent.CompletableFuture

object ServerRepository : Repository<ServerInfo>() {
    override fun table() = TableBuilder("servers")
        .column("uuid", DataType.UUID, primaryKey = true)
        .column("name", DataType.STRING, nullable = false)
        .column("type", DataType.STRING, nullable = false)
        .column("staging", DataType.BOOLEAN, nullable = false)
        .build()
    override fun map(rs: ResultSet): ServerInfo = ServerInfo(
            uuid = UUID.fromString(rs.getString("uuid")),
            name = rs.getString("name"),
            type = ServerType.valueOf(rs.getString("type")),
            staging = rs.getBoolean("staging")
        )
    override fun insertValues(entity: ServerInfo): List<Any> = listOf(
            entity.uuid.toString(),
            entity.name,
            entity.type.name,
            entity.staging
        )

    fun findByUUID(uuid: UUID): CompletableFuture<List<ServerInfo>> =
        database.query(
            "SELECT * FROM servers WHERE uuid = ?",
            listOf(uuid.toString()),
            ::map
        )
    fun findByName(name: String): CompletableFuture<List<ServerInfo>> =
        database.query(
            "SELECT * FROM servers WHERE name = ?",
            listOf(name),
            ::map
        )
    fun findByType(type: ServerType): CompletableFuture<List<ServerInfo>> =
        database.query(
            "SELECT * FROM servers WHERE type = ?",
            listOf(type.name),
            ::map
        )
    fun findByStaging(staging: Boolean): CompletableFuture<List<ServerInfo>> =
        database.query(
            "SELECT * FROM servers WHERE staging = ?",
            listOf(staging),
            ::map
        )
}