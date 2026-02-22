package net.minepact.api.messages.helper

fun Int.toHexString(): String = String.format("#%06X", this and 0xFFFFFF)