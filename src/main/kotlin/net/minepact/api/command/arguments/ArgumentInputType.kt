package net.minepact.api.command.arguments

import net.minepact.api.permissions.GroupRegistry
import net.minepact.api.player.PlayerRegistry
import org.bukkit.GameMode

/**
 * Represents the type of input expected for a command argument.
 * @author dankenyon - 22/02/26
 */
enum class ArgumentInputType {
    INTEGER,
    STRING,
    BOOLEAN,
    DOUBLE,
    FLOAT,
    LONG,
    PLAYER,
    GAMEMODE,
    GROUP;

    fun parse(input: String): Any? = try {
        when (this) {
            STRING -> input
            INTEGER -> input.toInt()
            DOUBLE -> input.toDouble()
            FLOAT -> input.toFloat()
            LONG -> input.toLong()
            BOOLEAN -> when {
                input.equals("true", true) -> true
                input.equals("false", true) -> false
                else -> null
            }
            PLAYER -> PlayerRegistry.get(input.lowercase()).get()
            GAMEMODE -> GameMode.valueOf(input.uppercase())
            GROUP -> GroupRegistry.get(input.lowercase()).get()
        }
    } catch (e: Exception) {
        null
    }
}