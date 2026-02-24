package net.minepact.core.global.commands.staff.punishment.helper

import net.minepact.Main
import net.minepact.api.data.repository.ServerRepository
import net.minepact.api.player.Player
import net.minepact.api.player.PlayerRegistry
import net.minepact.api.punishment.Punishment
import net.minepact.api.punishment.PunishmentType
import net.minepact.api.punishment.modifier.ScopeModifier
import org.bukkit.command.CommandSender
import java.util.UUID

fun createPunishment(
    player: Player,
    type: PunishmentType,
    target: UUID,
    reason: String,
    expiresAt: Long,
    scope: ScopeModifier
): Punishment {
    val servers = if (scope == ScopeModifier.GLOBAL) ServerRepository.findAll().get()
    else listOf(Main.SERVER.info)

    val targetIp: String? = null
    if (type == PunishmentType.IP_BAN) {

    }

    return Punishment(
        servers = servers.map { it.uuid },
        type = type,
        target = target,
        targetIp = targetIp,
        issuer = player.data.uuid,
        reason = reason,
        punishedAt = System.currentTimeMillis(),
        expiresAt = expiresAt
    )
}