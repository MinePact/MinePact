package net.minepact.api.event.custom

import net.minepact.api.event.CustomEvent
import org.bukkit.entity.Player

class PlayerChatEvent(
    val player: Player,
    val message: String
) : CustomEvent