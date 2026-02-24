package net.minepact.api.player

import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player as BukkitPlayer
import java.util.UUID

fun CommandSender.asPlayer(): Player {
    return when (this) {
        is BukkitPlayer -> PlayerRegistry.get(this.uniqueId).get()
        is ConsoleCommandSender -> PlayerRegistry.get(UUID(0, 0)).get()
        else -> throw IllegalStateException("CommandSender is not a Player or ConsoleCommandSender")
    }
}