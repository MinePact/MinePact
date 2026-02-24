package net.minepact.api.messages

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minepact.api.messages.actions.ClickAction
import net.minepact.api.messages.actions.HoverAction

data class Message(
    val time: Long = System.currentTimeMillis(),
    val components: List<MessageComponent>
) {
    fun toAdventureComponent(): Component {
        var result = Component.empty()

        for (mc in components) {
            var comp = FormatParser.parse(mc.text)
            mc.color?.let { hex ->
                val hexStr = String.format("#%06X", 0xFFFFFF and hex)
                comp = comp.color(TextColor.fromHexString(hexStr))
            }

            if (mc.bold) comp = comp.decorate(TextDecoration.BOLD)
            if (mc.italic) comp = comp.decorate(TextDecoration.ITALIC)
            if (mc.underlined) comp = comp.decorate(TextDecoration.UNDERLINED)
            if (mc.strikethrough) comp = comp.decorate(TextDecoration.STRIKETHROUGH)
            if (mc.obfuscated) comp = comp.decorate(TextDecoration.OBFUSCATED)

            mc.clickAction?.let { action ->
                val clickEvent = when (action) {
                    is ClickAction.OpenUrl -> ClickEvent.openUrl(action.value)
                    is ClickAction.RunCommand -> ClickEvent.runCommand(action.value)
                    is ClickAction.SuggestCommand -> ClickEvent.suggestCommand(action.value)
                }
                comp = comp.clickEvent(clickEvent)
            }
            mc.hoverAction?.let { hover ->
                when (hover) {
                    is HoverAction.ShowText -> {
                        val hoverComp = FormatParser.parse(hover.text)
                        comp = comp.hoverEvent(HoverEvent.showText(hoverComp))
                    }
                }
            }

            result = result.append(comp)
        }

        return result
    }
}
