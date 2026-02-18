package net.minepact.api.punishment

import java.util.UUID

class Punishment(
    val id: UUID,
    val targetServers: List<UUID>,
    val type: PunishmentType,
    val targetName: String,
    val issuerName: String,
    val reason: String,
    val punishedAt: Long,
    val expiresAt: Long
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
                issuer=$issuerName,
                reason=$reason,
                punishedAt=$punishedAt,
                expiresAt=$expiresAt,
            ]
        """.trimIndent()
    }
}