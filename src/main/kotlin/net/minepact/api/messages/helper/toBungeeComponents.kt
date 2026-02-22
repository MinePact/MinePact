package net.minepact.api.messages.helper

import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.chat.ComponentSerializer
import net.minepact.api.messages.actions.ClickAction
import net.minepact.api.messages.actions.HoverAction
import net.minepact.api.messages.Message

@Suppress("DEPRECATION")
fun Message.toBungeeComponents(): Array<BaseComponent> {
    val out = mutableListOf<BaseComponent>()
    val mini = MiniMessage.miniMessage()
    val gson = GsonComponentSerializer.gson()
    val legacySerializer = LegacyComponentSerializer.legacySection()

    for (comp in components) {
        val trimmed = comp.text.trimStart()
        val looksLikeMini = trimmed.contains('<') && trimmed.contains('>')
        val looksLikeJson = trimmed.startsWith("{") || trimmed.startsWith("[")

        if (looksLikeMini || looksLikeJson) {
            try {
                val adm = when {
                    looksLikeJson -> gson.deserialize(comp.text)
                    else -> mini.deserialize(comp.text)
                }

                // Serialize Adventure component to JSON and parse into Bungee BaseComponent[], preserving events
                val json = gson.serialize(adm)
                val parts = ComponentSerializer.parse(json)

                for (p in parts) {
                    // If MessageComponent defines a click action, override/add it
                    comp.clickAction?.let { ca ->
                        val click = when (ca) {
                            is ClickAction.OpenUrl -> ClickEvent(ClickEvent.Action.OPEN_URL, ca.value)
                            is ClickAction.RunCommand -> ClickEvent(ClickEvent.Action.RUN_COMMAND, ca.value)
                            is ClickAction.SuggestCommand -> ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, ca.value)
                        }
                        p.clickEvent = click
                    }

                    // If MessageComponent defines a hover action, try to parse it via Bungee JSON first, then fallback to legacy conversion
                    comp.hoverAction?.let { ha ->
                        when (ha) {
                            is HoverAction.ShowText -> {
                                val hoverComponents: Array<BaseComponent> = try {
                                    val hoverTrim = ha.text.trimStart()
                                    val hoverIsJson = hoverTrim.startsWith("{") || hoverTrim.startsWith("[")
                                    val hoverAdm = if (hoverIsJson) gson.deserialize(ha.text) else mini.deserialize(ha.text)
                                    val hoverJson = gson.serialize(hoverAdm)

                                    // First try parsing the Adventure JSON into Bungee components
                                    val parsed = try {
                                        ComponentSerializer.parse(hoverJson)
                                    } catch (_: Exception) {
                                        emptyArray<BaseComponent>()
                                    }

                                    if (parsed.isNotEmpty() && parsed.any { it.toPlainText().isNotBlank() }) {
                                        parsed
                                    } else {
                                        // Fallback: convert to legacy text then to TextComponent[]
                                        val hoverLegacy = legacySerializer.serialize(hoverAdm)
                                        TextComponent.fromLegacyText(hoverLegacy)
                                    }
                                } catch (_: Exception) {
                                    arrayOf(TextComponent(ha.text) as BaseComponent)
                                }

                                p.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponents)
                            }
                        }
                    }

                    out += p
                }

                continue
            } catch (_: Exception) {
                // Fall back to plain text below
            }
        }

        // Plain text handling (no MiniMessage/JSON)
        val tc = TextComponent(comp.text)

        // apply basic formatting from MessageComponent
        tc.isBold = comp.bold
        tc.isItalic = comp.italic
        tc.isUnderlined = comp.underlined
        tc.isStrikethrough = comp.strikethrough
        tc.isObfuscated = comp.obfuscated

        comp.clickAction?.let { ca ->
            val click = when (ca) {
                is ClickAction.OpenUrl -> ClickEvent(ClickEvent.Action.OPEN_URL, ca.value)
                is ClickAction.RunCommand -> ClickEvent(ClickEvent.Action.RUN_COMMAND, ca.value)
                is ClickAction.SuggestCommand -> ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, ca.value)
            }
            tc.clickEvent = click
        }

        comp.hoverAction?.let { ha ->
            when (ha) {
                is HoverAction.ShowText -> {
                    val hover = TextComponent(ha.text)
                    tc.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, arrayOf<BaseComponent>(hover))
                }
            }
        }

        out += tc as BaseComponent
    }

    return out.toTypedArray()
}
