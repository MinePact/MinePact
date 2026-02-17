package net.minepact.core.discord.embeds

import net.minepact.Main
import net.minepact.Main.Companion.SERVER
import net.minepact.api.discord.embed.Author
import net.minepact.api.discord.embed.Embed
import net.minepact.api.discord.embed.Field
import net.minepact.api.discord.embed.Footer
import java.text.SimpleDateFormat
import java.util.Date

fun restartEmbed() = Embed(
    author = Author(),
    title = ":orange_circle: Server Restarting",
    url = null,
    description = "${SERVER.info.name} is restarting!",
    colour = 0xEC8530,
    fields = listOf(
        Field("Name", SERVER.info.name, true),
        Field("Type", SERVER.info.type.name, true),
    ),
    thumbnail = null,
    image = null,
    footer = Footer("Server UUID: ${SERVER.info.uuid} | Time: ${SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        .format(Date(Main.SERVER_START_TIME))}")
)