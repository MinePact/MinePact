package net.minepact.core.global.events

import net.minepact.Main
import net.minepact.api.data.repository.PunishmentRepository
import net.minepact.api.event.EventContext
import net.minepact.api.event.SimpleEventHandler
import net.minepact.api.messages.send
import net.minepact.api.punishment.PunishmentType
import net.minepact.api.punishment.modifier.AnnouncementModifier
import net.minepact.core.global.commands.punishment.helper.message.getMuteAttemptMessage
import net.minepact.core.global.commands.punishment.helper.message.getPunishmentMessage
import org.bukkit.event.player.PlayerChatEvent
import kotlin.collections.forEach

@Suppress("DEPRECATION")
class PlayerChatHandler : SimpleEventHandler<PlayerChatEvent>() {
    override fun handle(context: EventContext<PlayerChatEvent>) {
        val event = context.event
        val player = event.player
        val message = event.message

        PunishmentRepository.findByTarget(event.player.name).get().forEach { punishment -> run {
            if ((System.currentTimeMillis() > punishment.expiresAt) && (punishment.expiresAt != Long.MIN_VALUE)) return@run
            if (punishment.type != PunishmentType.MUTE) return@run
            if (event.player.hasPermission("minepact.punishments.bypass.${punishment.type.name.lowercase()}")) return@run

            if (!punishment.reverted && punishment.targetServers.contains(Main.SERVER.info.uuid)) {
                event.isCancelled = true
                player.send(getMuteAttemptMessage(punishment))
            }
        } }

        event.format = "${player.name}: $message"
    }
}