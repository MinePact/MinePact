package net.minepact.api.event.custom

import net.minepact.api.event.CustomEvent
import net.minepact.api.player.Player

class NPCOpenMenuEvent(
    val player: Player,
    val menuName: String
) : CustomEvent