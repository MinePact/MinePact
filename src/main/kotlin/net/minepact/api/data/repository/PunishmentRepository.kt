package net.minepact.api.data.repository

import net.minepact.api.data.helper.DataType
import net.minepact.api.data.helper.TableBuilder
import net.minepact.api.punishment.Punishment
import net.minepact.api.punishment.PunishmentType
import java.sql.ResultSet
import java.util.UUID
import java.util.concurrent.CompletableFuture

class PunishmentRepository : Repository<Punishment>() {
    override fun table() = TableBuilder("punishments")
        .column("id", DataType.UUID, primaryKey = true)
        .column("servers", DataType.STRING, nullable = false)
        .column("type", DataType.STRING, nullable = false)
        .column("target", DataType.STRING, nullable = false)
        .column("issuer", DataType.STRING, nullable = false)
        .column("reason", DataType.STRING, nullable = false)
        .column("punished_at", DataType.LONG, nullable = false)
        .column("expires_at", DataType.LONG, nullable = false)
        .build()
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