package net.minepact.api.event.custom

import net.minepact.api.event.CustomEvent
import org.bukkit.GameMode

class PlayerChangeGameModeEvent(
    val playerName: String,
    val fromGameMode: GameMode,
    val toGameMode: GameMode
) : CustomEvent