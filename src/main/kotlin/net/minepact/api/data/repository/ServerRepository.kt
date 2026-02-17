package net.minepact.api.data.repository

import net.minepact.Main
import net.minepact.api.server.ServerInfo
import net.minepact.api.server.ServerType
import java.sql.ResultSet
import java.util.UUID
import java.util.concurrent.CompletableFuture

class ServerRepository : Repository<ServerInfo>() {
    override fun table() = "servers"
    override fun map(rs: ResultSet): ServerInfo {
        return ServerInfo(
            uuid = UUID.fromString(rs.getString("uuid")),
            name = rs.getString("name"),
            type = ServerType.valueOf(rs.getString("type")),
            staging = rs.getBoolean("staging")
        )
    }

    override fun insertColumns(): List<String> {
        return listOf("uuid", "name", "type", "staging")
    }
    override fun insertValues(entity: ServerInfo): List<Any> {
        return listOf(
            entity.uuid.toString(),
            entity.name,
            entity.type.name,
            entity.staging
        )
    }

    override fun ensureTableExists() {
        val sql = """
            CREATE TABLE IF NOT EXISTS servers (
                uuid CHAR(36) PRIMARY KEY,
                name VARCHAR(255) NOT NULL,
                type VARCHAR(50) NOT NULL,
                staging BOOLEAN NOT NULL
            )
        """.trimIndent()

        database.update(sql).thenAccept { Main.instance.logger.info("[ServerRepository] Table '${table()}' ensured.") }
            .exceptionally { ex ->
                ex.printStackTrace()
                null
            }
    }

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
    fun findByType(staging: Boolean): CompletableFuture<List<ServerInfo>> =
        database.query(
            "SELECT * FROM servers WHERE staging = ?",
            listOf(staging),
            ::map
        )
}