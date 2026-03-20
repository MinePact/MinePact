package net.minepact.core.global.commands.staff.punishment

import net.kyori.adventure.text.minimessage.MiniMessage
import net.minepact.Main
import net.minepact.api.command.CommandUsage
import net.minepact.api.command.Provider
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Argument
import net.minepact.api.command.arguments.ArgumentInputType
import net.minepact.api.command.arguments.ExpectedArgument
import net.minepact.api.data.repository.PunishmentRepository
import net.minepact.api.discord.Webhooks.PUNISHMENTS_WEBHOOK
import net.minepact.api.messages.send
import net.minepact.api.misc.formatDate
import net.minepact.api.player.PlayerRegistry
import net.minepact.api.permissions.Permission
import net.minepact.api.punishment.PunishmentType
import net.minepact.api.punishment.modifier.AnnouncementModifier
import net.minepact.api.punishment.modifier.ScopeModifier
import net.minepact.core.discord.embeds.punishments.banEmbed
import net.minepact.core.discord.embeds.punishments.kickEmbed
import net.minepact.core.global.commands.staff.punishment.helper.createPunishment
import net.minepact.core.global.commands.staff.punishment.helper.extractRawTokens
import net.minepact.core.global.commands.staff.punishment.helper.message.getPunishmentBroadcast
import net.minepact.core.global.commands.staff.punishment.helper.message.getPunishmentMessage
import net.minepact.core.global.commands.staff.punishment.helper.parseLength
import net.minepact.core.global.commands.staff.punishment.helper.parseReason
import net.minepact.core.global.commands.staff.punishment.helper.resolveAnnouncementModifier
import net.minepact.core.global.commands.staff.punishment.helper.resolveScopeModifier
import net.minepact.core.global.commands.staff.punishment.helper.retrieveModifiers
import net.minepact.core.global.commands.staff.punishment.helper.revertPunishment
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerKickEvent
import net.minepact.api.command.Command

class KickCommand : Command(
    name = "kick",
    description = "Kick a player from the server.",
    usage = CommandUsage(
        label = "kick", arguments = listOf(
            ExpectedArgument(name = "player", dynamicProvider = Provider.PLAYERS),
            ExpectedArgument(name = "reason", inputType = ArgumentInputType.STRING, optional = true),
            ExpectedArgument(name = "modifiers", dynamicProvider = Provider.EMPTY, optional = true)
        )
    ),
    permission = Permission("minepact.punishments.kick")
) {
    override fun execute(
        sender: net.minepact.api.player.Player, args: MutableList<Argument<*>>
    ): Result {
        val targetName: String = args[0].value as String
        try {
            PlayerRegistry.get(targetName)
        } catch (e: Exception) {
            sender.sendMessage("<red>Could not find <white>$targetName<red> in the database.")
            return Result.FAILURE
        }.thenAccept { player ->

            val rawTokens: MutableList<String> = extractRawTokens(args)
            val modifiers = retrieveModifiers(rawTokens)

            val (_, expiresAt) = parseLength(rawTokens)
            val reason = parseReason(rawTokens)

            val scope: ScopeModifier = resolveScopeModifier(modifiers)
            val announcement: AnnouncementModifier = resolveAnnouncementModifier(modifiers)

            val punishment = createPunishment(
                player = sender,
                type = PunishmentType.KICK,
                target = player.data.uuid,
                reason = reason,
                expiresAt = expiresAt,
                scope = scope
            )

            PunishmentRepository.insert(punishment)
            val broadcastMessage: String = getPunishmentBroadcast(punishment, announcement)

            Bukkit.getScheduler().runTask(Main.instance) { _ ->
                player.asPlayer()?.kick(
                    MiniMessage.miniMessage().deserialize(getPunishmentMessage(punishment, announcement)),
                    PlayerKickEvent.Cause.KICK_COMMAND
                )
            }
            when (announcement) {
                AnnouncementModifier.PUBLIC -> Bukkit.getOnlinePlayers().forEach { it.send(broadcastMessage) }
                AnnouncementModifier.SILENT -> Bukkit.getOnlinePlayers()
                    .forEach { if (it.hasPermission("minepact.punish.notify")) it.send(broadcastMessage) }
            }

            PUNISHMENTS_WEBHOOK.sendMessage("", listOf(kickEmbed(punishment, listOf(scope, announcement))))

        }
        return Result.SUCCESS
    }
}