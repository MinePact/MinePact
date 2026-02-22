package net.minepact.api.player.discord

fun generateSyncCode(): String {
    return (1..10)
        .map { "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".random() }
        .joinToString("")
}