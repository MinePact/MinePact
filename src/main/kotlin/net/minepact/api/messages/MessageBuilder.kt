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

    fun build(): Message = Message(components = components.toList())
}
