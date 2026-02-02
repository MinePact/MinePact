package net.minepact.api.command.arguments

sealed interface ArgumentRange<T : Number> {
    val min: T
    val max: T
}