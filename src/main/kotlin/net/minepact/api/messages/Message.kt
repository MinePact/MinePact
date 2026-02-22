package net.minepact.api.messages

data class Message(
    val time: Long = System.currentTimeMillis(),
    val components: List<MessageComponent>
)
