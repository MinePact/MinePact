package net.minepact.api.messages.helper

import net.minepact.api.messages.actions.ClickAction
import net.minepact.api.messages.actions.HoverAction
import net.minepact.api.messages.Message
import net.minepact.api.messages.MessageComponent
import java.util.regex.Pattern
import kotlin.collections.plusAssign
import kotlin.text.iterator

private val URL_REGEX: Pattern = Pattern.compile("(https?://\\S+)")

fun parseMessage(text: String): Message {
    val components = mutableListOf<MessageComponent>()
    val matcher = URL_REGEX.matcher(text)
    var lastEnd = 0

    while (matcher.find()) {
        val start = matcher.start()
        val end = matcher.end()
        if (start > lastEnd) {
            components += parseInlineFormatting(text.substring(lastEnd, start))
        }
        val url = text.substring(start, end)
        components += MessageComponent(
            text = url,
            underlined = true,
            clickAction = ClickAction.OpenUrl(url),
            hoverAction = HoverAction.ShowText("Click to open URL: $url")
        )
        lastEnd = end
    }

    if (lastEnd < text.length) {
        components += parseInlineFormatting(text.substring(lastEnd))
    }

    return Message(components = components)
}

private fun parseInlineFormatting(input: String): List<MessageComponent> {
    val result = mutableListOf<MessageComponent>()
    if (input.isEmpty()) return result

    var bold = false
    var italic = false
    var strike = false
    var obfuscated = false
    val sb = StringBuilder()

    fun flushBuffer() {
        if (sb.isEmpty()) return
        result += MessageComponent(
            text = sb.toString(),
            bold = bold,
            italic = italic,
            strikethrough = strike,
            obfuscated = obfuscated
        )
        sb.setLength(0)
    }

    for (c in input) {
        when (c) {
            '*' -> {
                flushBuffer()
                bold = !bold
            }
            '_' -> {
                flushBuffer()
                italic = !italic
            }
            '~' -> {
                flushBuffer()
                strike = !strike
            }
            '`' -> {
                flushBuffer()
                obfuscated = !obfuscated
            }
            else -> sb.append(c)
        }
    }
    flushBuffer()
    return result
}
