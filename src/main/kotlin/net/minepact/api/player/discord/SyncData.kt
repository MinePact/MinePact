package net.minepact.api.player.discord

import java.util.UUID

data class SyncData(
    val uuid: UUID,
    val code: String
)