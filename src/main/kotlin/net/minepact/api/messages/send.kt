package net.minepact.api.messages

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandSender

fun CommandSender.send(message: String) = this.sendMessage(MiniMessage.miniMessage().deserialize(message))
