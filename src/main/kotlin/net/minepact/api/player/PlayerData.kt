package net.minepact.api.player

import java.util.UUID

class PlayerData(
    val uuid: UUID,
    val name: String,
    var discordId: String,
    var nick: String = name,
    var chatColour: Int = 0xFFFFFF,
    val firstJoined: Long,
    var lastSeen: Long
)