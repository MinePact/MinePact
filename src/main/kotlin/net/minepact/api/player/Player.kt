package net.minepact.api.player

import net.minepact.Main
import net.minepact.api.messages.Message
import net.minepact.api.messages.helper.toBungeeComponents
import org.bukkit.Bukkit

class Player(
    val data: PlayerData,
    var online: Boolean
) {
    fun sendMessage(message: Message) {
        if (!online) return

        val bukkitPlayer = Bukkit.getPlayer(data.uuid)
        if (bukkitPlayer != null && bukkitPlayer.isOnline) {
            try {
                bukkitPlayer.spigot().sendMessage(*message.toBungeeComponents())
            } catch (ex: NoClassDefFoundError) {
                bukkitPlayer.sendMessage(message.components.joinToString(separator = "") { it.text })
            }
        } else {
            Main.instance.logger.info("[Message] Could not deliver to ${data.name}: ${message.components.joinToString(separator = "") { it.text }}")
        }
    }

    override fun toString(): String {
        return """
            Player[
                data=$data, 
                online=$online
            ]
        """.trimMargin()
    }
}