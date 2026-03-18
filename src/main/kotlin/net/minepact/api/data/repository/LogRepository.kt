package net.minepact.api.data.repository

import net.minepact.api.data.helper.DataType
import net.minepact.api.data.helper.TableBuilder
import net.minepact.api.logging.LogInfo
import net.minepact.api.logging.LogType
import java.sql.ResultSet
import java.util.UUID

object LogRepository : Repository<LogInfo>() {
    override fun table() = TableBuilder("logs")
        .column("id", DataType.INT, primaryKey = true, autoIncrement = true)
        .column("server", DataType.UUID, nullable = false)
        .column("sender", DataType.UUID, nullable = false)
        .column("type", DataType.STRING, nullable = false)
        .column("timestamp", DataType.LONG, nullable = false)
        .column("content", DataType.STRING, nullable = false)
        .column("suspicious", DataType.BOOLEAN, nullable = false)
        .build()
    override fun map(rs: ResultSet): LogInfo = LogInfo(
        serverId = UUID.fromString(rs.getString("server")),
        senderId = UUID.fromString(rs.getString("sender")),
        type = LogType.valueOf(rs.getString("type")),
        timestamp = rs.getLong("timestamp"),
        content = rs.getString("content"),
        suspicious = rs.getBoolean("suspicious")
    )
    override fun insertValues(entity: LogInfo): List<Any> = listOf(
        entity.serverId,
        entity.senderId,
        entity.type.name,
        entity.content,
        entity.serverId,
        entity.timestamp
    )
}