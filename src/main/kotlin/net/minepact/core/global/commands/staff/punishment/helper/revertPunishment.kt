package net.minepact.core.global.commands.staff.punishment.helper

import net.minepact.api.data.repository.PunishmentRepository
import net.minepact.api.punishment.Punishment
import java.util.UUID

fun revertPunishment(punishment: Punishment, revertedBy: UUID, revertReason: String): Punishment {
    return Punishment(
        id = punishment.id,
        servers = punishment.servers,
        type = punishment.type,
        target = punishment.target,
        targetIp = punishment.targetIp,
        issuer = punishment.issuer,
        reason = punishment.reason,
        punishedAt = punishment.punishedAt,
        expiresAt = punishment.expiresAt,
        reverted = true,
        revertedBy = revertedBy,
        revertedAt = System.currentTimeMillis(),
        revertReason = revertReason,
    )
}
fun revertPunishmentById(id: UUID, revertedBy: UUID, revertReason: String) {
    PunishmentRepository.findByID(id).thenAccept { punishments ->
        punishments.firstOrNull()?.let { punishment ->
            val reverted = revertPunishment(punishment, revertedBy, revertReason)
            PunishmentRepository.insert(reverted)
        }
    }
}