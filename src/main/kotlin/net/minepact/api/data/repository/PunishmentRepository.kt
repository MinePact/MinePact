package net.minepact.api.data.repository

import net.minepact.api.data.helper.DataType
import net.minepact.api.data.helper.TableBuilder
import net.minepact.api.punishment.Punishment
import net.minepact.api.punishment.PunishmentType
import java.sql.ResultSet
import java.util.UUID
import java.util.concurrent.CompletableFuture
import kotlin.toString

object PunishmentRepository : Repository<Punishment>() {
    override fun table() = TableBuilder("punishments")
        .column("id", DataType.UUID, primaryKey = true)
        .column("servers", DataType.STRING, nullable = false)
        .column("type", DataType.STRING, nullable = false)
        .column("target", DataType.STRING, nullable = false)
        .column("issuer", DataType.STRING, nullable = false)
        .column("reason", DataType.STRING, nullable = false)
        .column("punished_at", DataType.LONG, nullable = false)
        .column("expires_at", DataType.LONG, nullable = false)
        .column("reverted", DataType.BOOLEAN, defaultValue = false, nullable = false)
        .column("reverted_by", DataType.STRING, nullable = true)
        .column("reverted_at", DataType.LONG, nullable = true)
        .column("reverted_reason", DataType.STRING, nullable = true)
        .build()
    override fun map(rs: ResultSet): Punishment = Punishment(
        id = UUID.fromString(rs.getString("id")),
        targetServers = rs.getString("servers").split(",").filter { it.isNotEmpty() }.map { UUID.fromString(it) },
        type = PunishmentType.valueOf(rs.getString("type")),
        targetName = rs.getString("target"),
        issuerName = rs.getString("issuer"),
        reason = rs.getString("reason"),
        punishedAt = rs.getLong("punished_at"),
        expiresAt = rs.getLong("expires_at"),
        reverted = rs.getBoolean("reverted"),
        revertedBy = rs.getString("reverted_by").takeIf { it.isNotEmpty() },
        revertedAt = rs.getLong("reverted_at").takeIf { it != 0L },
        revertReason = rs.getString("reverted_reason").takeIf { it.isNotEmpty() }
    )
    override fun insertValues(entity: Punishment): List<Any> = listOf(
        entity.id.toString(),
        entity.targetServers.joinToString(",") { it.toString() },
        entity.type.name,
        entity.targetName,
        entity.issuerName,
        entity.reason,
        entity.punishedAt,
        entity.expiresAt,
        entity.reverted,
        entity.revertedBy ?: "",
        entity.revertedAt ?: 0L,
        entity.revertReason ?: ""
    )

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
    fun findActiveByTargetAndType(targetName: String, type: PunishmentType): CompletableFuture<Punishment?> =
        database.query(
            "SELECT * FROM punishments WHERE target = ? AND type = ? AND reverted = false AND expires_at > ? ORDER BY expires_at DESC LIMIT 1",
            listOf(targetName, type.name, System.currentTimeMillis()),
            ::map
        ).thenApply { it.firstOrNull() }
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