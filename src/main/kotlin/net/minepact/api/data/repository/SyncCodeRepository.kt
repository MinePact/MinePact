package net.minepact.api.data.repository

import net.minepact.api.data.helper.DataType
import net.minepact.api.data.helper.DatabaseTable
import net.minepact.api.data.helper.TableBuilder
import net.minepact.api.player.discord.SyncData
import java.sql.ResultSet
import java.util.UUID

object SyncCodeRepository : Repository<SyncData>() {
    override fun table(): DatabaseTable = TableBuilder("sync_codes")
        .column("uuid", DataType.UUID, primaryKey = true)
        .column("code", DataType.STRING, nullable = false)
        .build()
    override fun map(rs: ResultSet): SyncData = SyncData(
        uuid = UUID.fromString(rs.getString("uuid")),
        code = rs.getString("code")
    )
    override fun insertValues(entity: SyncData): List<Any> = listOf(
        entity.uuid,
        entity.code
    )

    fun findByUUID(uuid: UUID) = querySingle(
            "SELECT * FROM sync_codes WHERE uuid = ?",
            listOf(uuid.toString()),
            ::map
        )
}