package net.minepact.api.data.repository

import net.minepact.api.data.helper.DataType
import net.minepact.api.data.helper.TableBuilder
import net.minepact.api.player.PlayerData
import java.sql.ResultSet
import java.util.UUID
import java.util.concurrent.CompletableFuture

class PlayerRepository : Repository<PlayerData>() {
    override fun table() = TableBuilder("players")
        .column("uuid", DataType.UUID, primaryKey = true)
        .column("name", DataType.STRING, nullable = false)
        .column("nick", DataType.STRING, nullable = false)
        .column("chat_colour", DataType.INT, nullable = false)
        .column("first_joined", DataType.LONG, nullable = false)
        .column("last_joined", DataType.LONG, nullable = false)
        .build()
    override fun map(rs: ResultSet): PlayerData {
        return PlayerData(
            uuid = UUID.fromString(rs.getString("uuid")),
            name = rs.getString("name"),
            nick = rs.getString("nick"),
            chatColour = rs.getInt("chat_colour"),
            firstJoined = rs.getLong("first_joined"),
            lastJoined = rs.getLong("last_joined")
        )
    }
    override fun insertValues(entity: PlayerData): List<Any> {
        return listOf(
            entity.uuid.toString(),
            entity.name,
            entity.nick,
            entity.chatColour,
            entity.firstJoined,
            entity.lastJoined
        )
    }

    fun findByUUID(uuid: UUID): CompletableFuture<PlayerData?> =
        querySingle(
            "SELECT * FROM players WHERE uuid = ?",
            listOf(uuid.toString()),
            ::map
        )
    fun findByName(name: String): CompletableFuture<PlayerData?> =
        querySingle(
            "SELECT * FROM players WHERE LOWER(name) = ?",
            listOf(name),
            ::map
        )
}