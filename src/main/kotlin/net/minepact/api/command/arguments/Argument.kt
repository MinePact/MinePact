package net.minepact.api.command.arguments

class Argument<T : Any>(
    val raw: String,
    val value: T,
    val type: ArgumentInputType,
)