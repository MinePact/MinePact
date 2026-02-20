package net.minepact.core.global.commands.punishment.helper

import net.minepact.Main
import net.minepact.api.data.repository.ServerRepository
import net.minepact.api.punishment.Punishment
import net.minepact.api.punishment.modifier.PunishmentModifier
import net.minepact.api.punishment.PunishmentType
import net.minepact.api.punishment.modifier.ScopeModifier
import org.bukkit.command.CommandSender

fun createPunishment(
    sender: CommandSender,
    type: PunishmentType,
    targetName: String,
    reason: String,
    expiresAt: Long,
    scope: ScopeModifier
): Punishment {
    val servers = if (scope == ScopeModifier.GLOBAL) ServerRepository.findAll().get()
    else listOf(Main.SERVER.info)

    return Punishment(
        id = Punishment.generateId(),
        targetServers = servers.map { it.uuid },
        type = type,
        targetName = targetName,
        issuerName = sender.name,
        reason = reason,
        punishedAt = System.currentTimeMillis(),
        expiresAt = expiresAt
    )
}