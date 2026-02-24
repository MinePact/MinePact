package net.minepact.api.punishment

import java.util.UUID

class Punishment(
    val id: UUID = UUID.randomUUID(),
    val servers: List<UUID>,
    val type: PunishmentType,
    val target: UUID,
    val targetIp: String?,
    val issuer: UUID,
    val reason: String,
    val punishedAt: Long,
    val expiresAt: Long,
    val reverted: Boolean = false,
    val revertedBy: UUID? = null,
    val revertedAt: Long? = null,
    val revertReason: String? = null
) {
    override fun toString(): String {
        return """
            Punishment[
                id=$id,
                targetServers=${servers.map { it.toString() }},
                type=${type.name},
                target=$target,
                targetIp=$targetIp,
                issuer=$issuer,
                reason=$reason,
                punishedAt=$punishedAt,
                expiresAt=$expiresAt,
                reverted=$reverted,
                revertedBy=$revertedBy,
                revertedAt=$revertedAt,
                revertReason=$revertReason
            ]
        """.trimIndent()
    }
}