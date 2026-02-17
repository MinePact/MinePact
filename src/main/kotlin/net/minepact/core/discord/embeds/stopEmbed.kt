package net.minepact.core.discord.embeds

import net.minepact.Main.Companion.SERVER
import net.minepact.Main.Companion.SERVER_START_TIME
import net.minepact.api.discord.embed.Author
import net.minepact.api.discord.embed.Embed
import net.minepact.api.discord.embed.Field
import net.minepact.api.discord.embed.Footer
import net.minepact.api.misc.formatDuration
import java.text.SimpleDateFormat
import java.util.Date

fun stopEmbed() = Embed(
    author = Author(),
    title = ":red_circle: Server Stopped [${SERVER.info.name}]",
    url = null,
    description = "${SERVER.info.name} has stopped!",
    colour = 0xFF0000,
    fields = listOf(
        Field("Type", SERVER.info.type.name, true),
        Field("Up Time", formatDuration(System.currentTimeMillis() - SERVER_START_TIME), true)
    ),
    thumbnail = null,
    image = null,
    footer = Footer("Server UUID: ${SERVER.info.uuid} | Time: ${SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        .format(Date(SERVER_START_TIME))}")
)