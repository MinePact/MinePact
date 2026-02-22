package net.minepact.core.global.commands.punishment

import net.kyori.adventure.text.minimessage.MiniMessage
import net.minepact.Main
import net.minepact.api.command.Command
import net.minepact.api.command.CommandUsage
import net.minepact.api.command.Provider
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Argument
import net.minepact.api.command.arguments.ArgumentInputType
import net.minepact.api.command.arguments.ExpectedArgument
import net.minepact.api.data.repository.PunishmentRepository
import net.minepact.api.messages.send
import net.minepact.api.misc.formatDate
import net.minepact.api.punishment.PunishmentType
import net.minepact.api.punishment.modifier.AnnouncementModifier
import net.minepact.api.punishment.modifier.ScopeModifier
import net.minepact.core.discord.embeds.punishments.banEmbed
import net.minepact.core.discord.embeds.punishments.muteEmbed
import net.minepact.core.global.commands.punishment.helper.createPunishment
import net.minepact.core.global.commands.punishment.helper.retrieveModifiers
import net.minepact.core.global.commands.punishment.helper.extractRawTokens
import net.minepact.core.global.commands.punishment.helper.message.getPunishmentBroadcast
import net.minepact.core.global.commands.punishment.helper.message.getPunishmentMessage
import net.minepact.core.global.commands.punishment.helper.parseLength
import net.minepact.core.global.commands.punishment.helper.parseReason
import net.minepact.core.global.commands.punishment.helper.resolveAnnouncementModifier
import net.minepact.core.global.commands.punishment.helper.resolveScopeModifier
import net.minepact.core.global.commands.punishment.helper.revertPunishment
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerKickEvent
import kotlin.collections.mutableListOf

class MuteCommand : Command(
    name = "mute",
    description = "Mute a player from the server.",
    permission = "minepact.punishments.mute",
    aliases = mutableListOf(),
    usage = CommandUsage(
        label = "mute", arguments = listOf(
            ExpectedArgument(name = "player", dynamicProvider = Provider.PLAYERS),
            ExpectedArgument(
                name = "length",
                potentialValues = listOf("30m", "1h", "2h", "8h", "1d", "3d", "7d"),
                inputType = ArgumentInputType.STRING,
                optional = true
            ),
            ExpectedArgument(name = "reason", inputType = ArgumentInputType.STRING, optional = true),
            ExpectedArgument(name = "modifiers", dynamicProvider = Provider.EMPTY, optional = true)
        )
    ),
    cooldown = 1.0
) {
    override fun execute(
        sender: CommandSender,
        args: MutableList<Argument<*>>
    ): Result {
        val targetName: String = args[0].value as String
        val target: Player? = Bukkit.getPlayer(targetName)

        val rawTokens: MutableList<String> = extractRawTokens(args)
        val modifiers = retrieveModifiers(rawTokens)

        val (_, expiresAt) = parseLength(rawTokens)
        val reason = parseReason(rawTokens)

        val scope: ScopeModifier = resolveScopeModifier(modifiers)
        val announcement: AnnouncementModifier = resolveAnnouncementModifier(modifiers)

        val punishment = createPunishment(
            sender = sender,
            type = PunishmentType.MUTE,
            targetName = targetName,
            reason = reason,
            expiresAt = expiresAt,
            scope = scope
        )

        PunishmentRepository.findActiveByTargetAndType(targetName, PunishmentType.MUTE).thenAccept { activeMute ->
            if (activeMute != null && activeMute.expiresAt >= expiresAt && expiresAt != Long.MIN_VALUE) {
                sender.send("<white>$targetName <red>is already muted. Their current mute expires at <white>${formatDate(activeMute.expiresAt)}<red>.")
                return@thenAccept
            }

            if (activeMute != null && activeMute.expiresAt < expiresAt) {
                val reverted = revertPunishment(activeMute, sender.name, "OTHER_MUTE_OVERRIDE")
                PunishmentRepository.insert(reverted)
            }

            PunishmentRepository.insert(punishment)
            val targetMessage: String = getPunishmentMessage(punishment, announcement)
            val broadcastMessage: String = getPunishmentBroadcast(punishment, announcement)

            target?.send(targetMessage)
            when (announcement) {
                AnnouncementModifier.PUBLIC -> Bukkit.getOnlinePlayers().forEach { it.send(broadcastMessage) }
                AnnouncementModifier.SILENT -> Bukkit.getOnlinePlayers().forEach { if (it.hasPermission("minepact.punish.notify")) it.send(broadcastMessage) }
            }

            Main.PUNISHMENTS_WEBHOOK.sendMessage("", listOf(muteEmbed(punishment, listOf(scope, announcement))))
        }
        return Result.SUCCESS
    }
}
