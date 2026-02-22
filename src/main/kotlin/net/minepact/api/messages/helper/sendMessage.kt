package net.minepact.api.messages.helper

import net.minepact.api.messages.Message
import net.minepact.api.messages.MessageBuilder

fun message(init: MessageBuilder.() -> Unit): Message = MessageBuilder().apply(init).build()