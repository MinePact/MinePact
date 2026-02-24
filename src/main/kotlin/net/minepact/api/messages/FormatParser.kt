package net.minepact.api.messages

import net.kyori.adventure.text.*
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.*
import java.awt.Color
import kotlin.math.roundToInt

object FormatParser {
    fun parse(message: String): Component {
        return parseSection(message)
    }

    private fun parseSection(input: String): Component {
        var text = input
        var component = Component.empty()

        while (text.isNotEmpty()) {

            val tagStart = text.indexOf("<")
            if (tagStart == -1) {
                component = component.append(Component.text(text))
                break
            }

            if (tagStart > 0) {
                component = component.append(Component.text(text.take(tagStart)))
                text = text.substring(tagStart)
            }

            val tagEnd = text.indexOf(">")
            if (tagEnd == -1) break

            val rawTag = text.substring(1, tagEnd)
            text = text.substring(tagEnd + 1)

            when {
                rawTag.startsWith("url:") -> {
                    val url = rawTag.removePrefix("url:")
                    val inner = extractInner(text, "url")
                    component = component.append(
                        parseSection(inner.content)
                            .clickEvent(ClickEvent.openUrl(url))
                            .hoverEvent(HoverEvent.showText(Component.text(url)))
                    )
                    text = inner.remaining
                }

                rawTag.startsWith("click:") -> {
                    val parts = rawTag.split(":", limit = 3)
                    val type = parts[1]
                    val value = parts[2]

                    val inner = extractInner(text, "click")
                    var comp = parseSection(inner.content)

                    comp = when (type.lowercase()) {
                        "run" -> comp.clickEvent(ClickEvent.runCommand(value))
                        "suggest" -> comp.clickEvent(ClickEvent.suggestCommand(value))
                        "copy" -> comp.clickEvent(ClickEvent.copyToClipboard(value))
                        else -> comp
                    }

                    component = component.append(comp)
                    text = inner.remaining
                }

                rawTag.startsWith("hover:") -> {
                    var hoverText = rawTag.removePrefix("hover:")

                    if (hoverText.startsWith("'") && hoverText.endsWith("'") && hoverText.length >= 2) {
                        hoverText = hoverText.substring(1, hoverText.length - 1)
                    }

                    val inner = extractInner(text, "hover")

                    component = component.append(
                        parseSection(inner.content).hoverEvent(HoverEvent.showText(parseSection(hoverText)))
                    )

                    text = inner.remaining
                }

                rawTag.startsWith("hex:") -> {
                    val hex = rawTag.removePrefix("hex:")
                    val inner = extractInner(text, "hex:$hex")

                    component = component.append(
                        parseSection(inner.content)
                            .color(TextColor.fromHexString("#$hex"))
                    )
                    text = inner.remaining
                }

                rawTag.startsWith("from:") -> {
                    val start = rawTag.removePrefix("from:")
                    val endTag = "<to:"
                    val endIndex = text.indexOf(endTag)

                    val content = text.substring(0, endIndex)
                    val endHex = text.substring(endIndex + 4, endIndex + 10)

                    component = component.append(
                        gradient(content, start, endHex)
                    )

                    text = text.substring(endIndex + 11)
                }

                MessageColour.fromTag(rawTag) != null -> {
                    val colour = MessageColour.fromTag(rawTag)!!
                    val inner = extractInner(text, rawTag)

                    var parsed = parseSection(inner.content)

                    toDecoration(colour)?.let { parsed = parsed.decorate(it) }
                    toColor(colour)?.let { parsed = parsed.colorIfAbsent(it) }

                    component = component.append(parsed)

                    text = inner.remaining
                }
            }
        }

        return component
    }

    private fun extractInner(text: String, tag: String): InnerResult {
        val close = "</$tag>"
        val endIndex = text.indexOf(close)

        if (endIndex == -1) {
            return InnerResult(text, "")
        }

        val content = text.substring(0, endIndex)
        val remaining = text.substring(endIndex + close.length)

        return InnerResult(content, remaining)
    }

    private fun gradient(text: String, startHex: String, endHex: String): Component {
        val start = Color.decode("#$startHex")
        val end = Color.decode("#$endHex")

        val builder = Component.empty()
        val length = text.length

        var comp = builder
        for (i in text.indices) {
            val ratio = i.toFloat() / (length - 1).coerceAtLeast(1)

            val red = (start.red + (end.red - start.red) * ratio).roundToInt()
            val green = (start.green + (end.green - start.green) * ratio).roundToInt()
            val blue = (start.blue + (end.blue - start.blue) * ratio).roundToInt()

            comp = comp.append(
                Component.text(text[i])
                    .color(TextColor.color(red, green, blue))
            )
        }

        return comp
    }

    private fun toColor(colour: MessageColour): TextColor? {
        return when (colour) {
            MessageColour.RED -> NamedTextColor.RED
            MessageColour.BLUE -> NamedTextColor.BLUE
            MessageColour.GREEN -> NamedTextColor.GREEN
            MessageColour.YELLOW -> NamedTextColor.YELLOW
            MessageColour.GRAY -> NamedTextColor.GRAY
            MessageColour.DARK_RED -> NamedTextColor.DARK_RED
            MessageColour.DARK_BLUE -> NamedTextColor.DARK_BLUE
            MessageColour.DARK_GREEN -> NamedTextColor.DARK_GREEN
            MessageColour.DARK_AQUA -> NamedTextColor.DARK_AQUA
            MessageColour.DARK_PURPLE -> NamedTextColor.DARK_PURPLE
            MessageColour.GOLD -> NamedTextColor.GOLD
            MessageColour.DARK_GRAY -> NamedTextColor.DARK_GRAY
            MessageColour.AQUA -> NamedTextColor.AQUA
            MessageColour.LIGHT_PURPLE -> NamedTextColor.LIGHT_PURPLE
            MessageColour.WHITE -> NamedTextColor.WHITE
            else -> null
        }
    }
    private fun toDecoration(colour: MessageColour): TextDecoration? {
        return when (colour) {
            MessageColour.BOLD -> TextDecoration.BOLD
            MessageColour.ITALIC -> TextDecoration.ITALIC
            MessageColour.UNDERLINE -> TextDecoration.UNDERLINED
            MessageColour.STRIKETHROUGH -> TextDecoration.STRIKETHROUGH
            MessageColour.OBFUSCATED -> TextDecoration.OBFUSCATED
            else -> null
        }
    }

    private data class InnerResult(
        val content: String,
        val remaining: String
    )
}