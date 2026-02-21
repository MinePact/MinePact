package net.minepact.core.global.commands.punishment.helper

import net.minepact.api.data.repository.PunishmentRepository
import net.minepact.api.punishment.Punishment
import java.util.UUID

fun revertPunishment(punishment: Punishment, revertedBy: String, revertReason: String): Punishment {
    return Punishment(
        id = punishment.id,
        targetServers = punishment.targetServers,
        type = punishment.type,
        targetName = punishment.targetName,
        targetIp = punishment.targetIp,
        issuerName = punishment.issuerName,
        reason = punishment.reason,
        punishedAt = punishment.punishedAt,
        expiresAt = punishment.expiresAt,
        reverted = true,
        revertedBy = revertedBy,
        revertedAt = System.currentTimeMillis(),
        revertReason = revertReason,
    )
}
fun revertPunishmentById(id: UUID, revertedBy: String, revertReason: String) {
    PunishmentRepository.findByID(id).thenAccept { punishments ->
        punishments.firstOrNull()?.let { punishment ->
            val reverted = revertPunishment(punishment, revertedBy, revertReason)
            PunishmentRepository.insert(reverted)
        }
    }
}