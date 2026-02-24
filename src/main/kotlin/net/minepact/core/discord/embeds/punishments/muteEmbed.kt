package net.minepact.core.discord.embeds.punishments

import net.minepact.Main.Companion.SERVER
import net.minepact.api.discord.embed.Author
import net.minepact.api.discord.embed.Embed
import net.minepact.api.discord.embed.Footer
import net.minepact.api.misc.formatDate
import net.minepact.api.misc.formatDuration
import net.minepact.api.player.PlayerRegistry
import net.minepact.api.punishment.Punishment
import net.minepact.api.punishment.modifier.PunishmentModifier
import net.minepact.api.punishment.modifier.ScopeModifier
import java.text.SimpleDateFormat
import java.util.Date

fun muteEmbed(punishment: Punishment, modifiers: List<PunishmentModifier>) = Embed(
        author = Author(),
        title = "${PlayerRegistry.get(punishment.target).get().data.name} has been muted",
        url = null,
        descriptionLines = listOf(
            "",
            "**Target Server**: ${
                if (modifiers.contains(ScopeModifier.GLOBAL)) "Network"
                else SERVER.info.name
            }",
            "**Staff**: ${PlayerRegistry.get(punishment.issuer).get().data.name}",
            "**Reason**: ${punishment.reason}",
            "**Length**: ${
                if (punishment.expiresAt == Long.MIN_VALUE) "Permanent" 
                else formatDuration(punishment.expiresAt - System.currentTimeMillis())
            }",
            "**Expires**: ${formatDate(punishment.expiresAt)}",
            "**Modifiers**: ${modifiers[0]}, ${modifiers[1]}"
        ),
        colour = 0xFFA200,
        fields = listOf(),
        thumbnail = null,
        image = null,
        footer = Footer("Server UUID: ${SERVER.info.uuid} | Time: ${SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
            .format(Date(System.currentTimeMillis()))}")
    )
fun unmuteEmbed(punishment: Punishment, modifiers: List<PunishmentModifier>) = Embed(
    author = Author(),
    title = "${PlayerRegistry.get(punishment.target).get().data.name} has been unmuted",
    url = null,
    descriptionLines = listOf(
        "",
        "**Target Server**: ${
            if (modifiers.contains(ScopeModifier.GLOBAL)) "Network"
            else SERVER.info.name
        }",
        "**Staff**: ${PlayerRegistry.get(punishment.revertedBy!!).get().data.name}",
        "**Reason**: ${punishment.revertReason}",
        "**Modifiers**: ${modifiers[0]}, ${modifiers[1]}"
    ),
    colour = 0xFFCB70,
    fields = listOf(),
    thumbnail = null,
    image = null,
    footer = Footer("Server UUID: ${SERVER.info.uuid} | Time: ${SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        .format(Date(System.currentTimeMillis()))}")
)