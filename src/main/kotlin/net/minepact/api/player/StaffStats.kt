package net.minepact.api.player

import java.util.UUID

data class StaffStats(
    val uuid: UUID,
    val staffTime: Long,
    val bans: Int,
    val mutes: Int,
    val warns: Int,
    val kicks: Int
)