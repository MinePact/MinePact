package net.minepact.api.messages.helper

import net.minepact.api.messages.*

/*
 * ```kotlin
 *
 * val m = msg {
 *   +"Hello world"                             // plain component
 *   text("Click me") {                         // component with config
 *     color(0xFFAA00)
 *     bold()
 *     clickOpenUrl("https://example.com")
 *     hoverText("This is a link")
 *   }
 *   url("Visit", "https://example.com")
 * }
 *
 * ```
 */
fun msg(init: MessageDsl.() -> Unit): Message = MessageBuilder().let { mb ->
    MessageDsl(mb).apply(init).build()
}

class MessageDsl(private val builder: MessageBuilder) {
    operator fun String.unaryPlus() { builder.append(this) }
    operator fun String.unaryMinus() { builder.append(this+"\n") }

    fun text(text: String, init: ComponentDsl.() -> Unit = {}) {
        builder.append(text) { ComponentDsl(this).init() }
    }
    fun url(text: String, url: String) { builder.url(text, url) }

    fun replace(text: String, replacement: String) = apply { builder.replace(text, replacement) }
    fun replace(vararg replacements: Pair<String, String>) = apply { builder.replace(*replacements) }

    fun build(): Message = builder.build()
}

class ComponentDsl(private val cb: ComponentBuilder) {
    fun color(hex: Int) = apply { cb.color(hex) }
    fun hex(hex: String) = apply { cb.color(hex.removePrefix("#").toInt(16)) }

    fun bold() = apply { cb.bold() }
    fun italic() = apply { cb.italic() }
    fun underlined() = apply { cb.underlined() }
    fun underlined(flag: Boolean) = apply { cb.underlined(flag) }
    fun strikethrough() = apply { cb.strikethrough() }
    fun obfuscated() = apply { cb.obfuscated() }

    fun clickOpenUrl(url: String) = apply { cb.clickOpenUrl(url) }
    fun clickRunCommand(cmd: String) = apply { cb.clickRunCommand(cmd) }
    fun clickSuggestCommand(cmd: String) = apply { cb.clickSuggestCommand(cmd) }
    fun clickCopy(value: String) = apply { cb.clickCopy(value) }

    fun hoverText(vararg lines: String) = apply { cb.hoverText(*lines) }
}
