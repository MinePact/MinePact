package net.minepact.core.discord.embeds.punishments

import net.minepact.Main.Companion.SERVER
import net.minepact.api.discord.embed.Author
import net.minepact.api.discord.embed.Embed
import net.minepact.api.discord.embed.Footer
import net.minepact.api.misc.formatDate
import net.minepact.api.misc.formatDuration
import net.minepact.api.punishment.Punishment
import net.minepact.api.punishment.modifier.PunishmentModifier
import net.minepact.api.punishment.modifier.ScopeModifier
import java.text.SimpleDateFormat
import java.util.Date

fun kickEmbed(punishment: Punishment, modifiers: List<PunishmentModifier>) = Embed(
        author = Author(),
        title = "${punishment.targetName} has been kicked",
        url = null,
        descriptionLines = listOf(
            "",
            "**Target Server**: ${
                if (modifiers.contains(ScopeModifier.GLOBAL)) "Network"
                else SERVER.info.name
            }",
            "**Staff**: ${punishment.issuerName}",
            "**Reason**: ${punishment.reason}",
            "**Modifiers**: ${modifiers[0]}, ${modifiers[1]}"
        ),
        colour = 0x0040FF,
        fields = listOf(),
        thumbnail = null,
        image = null,
        footer = Footer("Server UUID: ${SERVER.info.uuid} | Time: ${SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
            .format(Date(System.currentTimeMillis()))}")
    )