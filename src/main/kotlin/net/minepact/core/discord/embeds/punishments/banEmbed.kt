package net.minepact.core.discord.embeds.punishments

import net.minepact.Main
import net.minepact.Main.Companion.SERVER
import net.minepact.api.discord.embed.Author
import net.minepact.api.discord.embed.Embed
import net.minepact.api.discord.embed.Footer
import net.minepact.api.misc.formatDate
import net.minepact.api.misc.formatDuration
import net.minepact.api.punishment.Punishment
import net.minepact.api.punishment.PunishmentModifiers
import java.text.SimpleDateFormat
import java.util.Date

fun banEmbed(punishment: Punishment, modifiers: List<PunishmentModifiers>) = Embed(
        author = Author(),
        title = "${punishment.targetName} has been banned!",
        url = null,
        description = "\n**Staff**: ${punishment.issuerName}\n**Reason**: ${punishment.reason}\n**Length**: ${
            if (punishment.expiresAt == Long.MIN_VALUE) "Permanent" else formatDuration(punishment.expiresAt - System.currentTimeMillis())
        }\n**Expires At**: ${formatDate(punishment.expiresAt)}\n**Modifiers**: ${modifiers[0]}, ${modifiers[1]}",
        colour = 0xFF0000,
        fields = listOf(),
        thumbnail = null,
        image = null,
        footer = Footer("Server UUID: ${SERVER.info.uuid} | Time: ${SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
            .format(Date(System.currentTimeMillis()))}")
    )