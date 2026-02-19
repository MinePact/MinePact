package net.minepact.api.data.repository

import net.minepact.Main
import net.minepact.api.punishment.Punishment
import net.minepact.api.punishment.PunishmentType
import net.minepact.api.server.ServerInfo
import net.minepact.api.server.ServerType
import java.sql.ResultSet
import java.util.UUID
import java.util.concurrent.CompletableFuture

class PunishmentRepository : Repository<Punishment>() {
    override fun table() = "punishments"
    override fun map(rs: ResultSet): Punishment {
        return Punishment(
            id = UUID.fromString(rs.getString("id")),
            targetServers = rs.getString("servers")
                .replace("[", "")
                .replace("]", "")
                .split(",")
                .map { it.trim() }
                .map { UUID.fromString(it) },
            type = PunishmentType.valueOf(rs.getString("type")),
            targetName = rs.getString("target"),
            issuerName = rs.getString("issuer"),
            reason = rs.getString("reason"),
            punishedAt = rs.getLong("punished_at"),
            expiresAt = rs.getLong("expires_at")
        )
    }
    override fun insertColumns(): List<String> {
        return listOf("id", "servers", "type", "target", "issuer", "reason", "punished_at", "expires_at")
    }
    override fun insertValues(entity: Punishment): List<Any> {
        return listOf(
            entity.id,
            entity.targetServers.toString(),
            entity.type.name,
            entity.targetName,
            entity.issuerName,
            entity.reason,
            entity.punishedAt,
            entity.expiresAt
        )
    }

    override fun ensureTableExists() {
        val sql = """
            CREATE TABLE IF NOT EXISTS punishments (
                id CHAR(36) PRIMARY KEY,
                servers VARCHAR(255) NOT NULL,
                type VARCHAR(255) NOT NULL,
                target VARCHAR(255) NOT NULL,
                issuer VARCHAR(255) NOT NULL,
                reason VARCHAR(255) NOT NULL,
                punished_at BIGINT(255) NOT NULL,
                expires_at BIGINT(255) NOT NULL
            )
        """.trimIndent()

        database.update(sql).thenAccept { Main.instance.logger.info("[PunishmentRepository] Table '${table()}' ensured.") }
            .exceptionally { ex ->
                ex.printStackTrace()
                null
            }
    }

    fun findByID(id: UUID): CompletableFuture<List<Punishment>> =
        database.query(
            "SELECT * FROM punishments WHERE id = ?",
            listOf(id.toString()),
            ::map
        )

    fun findByTarget(targetName: String): CompletableFuture<List<Punishment>> =
        database.query(
            "SELECT * FROM punishments WHERE target = ?",
            listOf(targetName),
            ::map
        )

    fun findByIssuer(issuerName: String): CompletableFuture<List<Punishment>> =
        database.query(
            "SELECT * FROM punishments WHERE issuer = ?",
            listOf(issuerName),
            ::map
        )

    fun findByServer(serverId: UUID): CompletableFuture<List<Punishment>> =
        database.query(
            "SELECT * FROM punishments WHERE servers LIKE ?",
            listOf("%${serverId}%"),
            ::map
        )
}