package net.minepact.api.punishment

import java.util.UUID

class Punishment(
    val id: UUID,
    val targetServers: List<UUID>,
    val type: PunishmentType,
    val targetName: String,
    val targetIp: String?,
    val issuerName: String,
    val reason: String,
    val punishedAt: Long,
    val expiresAt: Long,
    val reverted: Boolean = false,
    val revertedBy: String? = null,
    val revertedAt: Long? = null,
    val revertReason: String? = null
) {
    companion object {
        fun generateId(): UUID {
            return UUID.randomUUID()
        }
    }

    override fun toString(): String {
        return """
            Punishment[
                id=$id,
                targetServers=${targetServers.map { it.toString() }},
                type=${type.name},
                target=$targetName,
                targetIp=$targetIp,
                issuer=$issuerName,
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