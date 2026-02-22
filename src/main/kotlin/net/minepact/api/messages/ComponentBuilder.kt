package net.minepact.api.messages

import net.minepact.api.messages.actions.ClickAction
import net.minepact.api.messages.actions.HoverAction

class ComponentBuilder(
    private val text: String
) {
    private var color: Int? = null
    private var bold: Boolean = false
    private var italic: Boolean = false
    private var underlined: Boolean = false
    private var strikethrough: Boolean = false
    private var obfuscated: Boolean = false
    private var clickAction: ClickAction? = null
    private var hoverAction: HoverAction? = null

    fun color(hex: Int) = apply { this.color = hex }
    fun bold() = apply { this.bold = true }
    fun italic() = apply { this.italic = true }
    fun underlined() = apply { this.underlined = true }
    fun strikethrough() = apply { this.strikethrough = true }
    fun obfuscated() = apply { this.obfuscated = true }

    fun underlined(flag: Boolean) = apply { this.underlined = flag }

    fun clickOpenUrl(url: String) = apply { this.clickAction = ClickAction.OpenUrl(url) }
    fun clickRunCommand(cmd: String) = apply { this.clickAction = ClickAction.RunCommand(cmd) }
    fun clickSuggestCommand(cmd: String) = apply { this.clickAction = ClickAction.SuggestCommand(cmd) }

    fun hoverText(text: String) = apply { this.hoverAction = HoverAction.ShowText(text) }
    fun hoverText(vararg text: String) = apply { this.hoverAction = HoverAction.ShowText(text.joinToString(separator = "\n")) }

    fun build(): MessageComponent = MessageComponent(
        text = text,
        color = color,
        bold = bold,
        italic = italic,
        underlined = underlined,
        strikethrough = strikethrough,
        obfuscated = obfuscated,
        clickAction = clickAction,
        hoverAction = hoverAction
    )
}