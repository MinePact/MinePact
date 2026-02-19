package net.minepact.core.global.commands.punishment.helper

import net.minepact.Main
import net.minepact.api.punishment.Punishment
import net.minepact.api.punishment.PunishmentModifiers
import net.minepact.api.punishment.PunishmentType
import org.bukkit.command.CommandSender

fun createPunishment(
    sender: CommandSender,
    type: PunishmentType,
    targetName: String,
    reason: String,
    expiresAt: Long,
    scope: PunishmentModifiers
): Punishment {
    val servers = if (scope == PunishmentModifiers.GLOBAL) Main.SERVER_REPOSITORY.findAll().get()
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