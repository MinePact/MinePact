package net.minepact.core.discord.embeds.punishments

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

fun warnEmbed(punishment: Punishment, modifiers: List<PunishmentModifiers>) = Embed(
        author = Author(),
        title = "${punishment.targetName} has been warned",
        url = null,
        descriptionLines = listOf(
            "",
            "**Target Server**: ${
                if (modifiers.contains(PunishmentModifiers.GLOBAL)) "Network"
                else SERVER.info.name
            }",
            "**Staff**: ${punishment.issuerName}",
            "**Reason**: ${punishment.reason}",
            "**Length**: ${
                if (punishment.expiresAt == Long.MIN_VALUE) "Permanent" 
                else formatDuration(punishment.expiresAt - System.currentTimeMillis())
            }",
            "**Expires**: ${formatDate(punishment.expiresAt)}",
            "**Modifiers**: ${modifiers[0]}, ${modifiers[1]}"
        ),
        colour = 0xECFF85,
        fields = listOf(),
        thumbnail = null,
        image = null,
        footer = Footer("Server UUID: ${SERVER.info.uuid} | Time: ${SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
            .format(Date(System.currentTimeMillis()))}")
    )

fun unwarnEmbed(punishment: Punishment, modifiers: List<PunishmentModifiers>) = Embed(
    author = Author(),
    title = "${punishment.targetName} has been unwarned",
    url = null,
    descriptionLines = listOf(
        "",
        "**Target Server**: ${
            if (modifiers.contains(PunishmentModifiers.GLOBAL)) "Network"
            else SERVER.info.name
        }",
        "**Staff**: ${punishment.issuerName}",
        "**Reason**: ${punishment.reason}",
        "**Modifiers**: ${modifiers[0]}, ${modifiers[1]}"
    ),
    colour = 0xF1F1A1,
    fields = listOf(),
    thumbnail = null,
    image = null,
    footer = Footer("Server UUID: ${SERVER.info.uuid} | Time: ${SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        .format(Date(System.currentTimeMillis()))}")
)