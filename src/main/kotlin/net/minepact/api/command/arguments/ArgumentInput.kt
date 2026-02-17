package net.minepact.api.command.arguments

// ignore for now
abstract class ArgumentInput<T> {
    abstract fun parseInput(values: List<T>): List<String>
}