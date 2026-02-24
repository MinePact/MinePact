@file:Suppress("unused")

package net.minepact.api.messages

import net.minepact.api.messages.actions.ClickAction
import net.minepact.api.messages.actions.HoverAction

data class MessageComponent(
    var text: String,
    val color: Int? = null,
    val bold: Boolean = false,
    val italic: Boolean = false,
    val underlined: Boolean = false,
    val strikethrough: Boolean = false,
    val obfuscated: Boolean = false,
    val clickAction: ClickAction? = null,
    val hoverAction: HoverAction? = null
)
