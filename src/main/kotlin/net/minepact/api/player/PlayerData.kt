package net.minepact.api.player

import java.util.UUID

class PlayerData(
    val uuid: UUID,
    val name: String,
    var ipHistory: List<String>,
    var discordId: String,
    var nick: String = name,
    var chatColour: Int = 0xFFFFFF,
    val firstJoined: Long,
    var lastSeen: Long
) {
    override fun toString(): String {
        return """
            PlayerData(
                uuid=$uuid,
                name='$name',
                ipHistory=$ipHistory,
                discordId='$discordId',
                nick='$nick',
                chatColour=$chatColour,
                firstJoined=$firstJoined,
                lastSeen=$lastSeen
            )
        """.trimIndent()
    }
}