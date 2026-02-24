@file:Suppress("unused")

package net.minepact.api.messages

class MessageBuilder {
    private val components = mutableListOf<MessageComponent>()

    fun append(text: String, init: (ComponentBuilder.() -> Unit)? = null): MessageBuilder {
        val cb = ComponentBuilder(text).apply { init?.invoke(this) }
        components += cb.build()
        return this
    }

    fun url(text: String, url: String): MessageBuilder {
        val cb = ComponentBuilder(text)
        cb.clickOpenUrl(url)
        cb.underlined(true)
        components += cb.build()
        return this
    }

    fun replace(text: String, replacement: String): MessageBuilder {
        for (component in components) {
            component.text.replace(text, replacement)
        }
        return this
    }
    fun replace(vararg replacements: Pair<String, String>): MessageBuilder {
        for (component in components) {
            for (replacement in replacements) {
                component.text = component.text.replace(replacement.first, replacement.second)
            }
        }
        return this
    }

    fun build(): Message = Message(components = components.toList())
}
