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
import net.minepact.api.punishment.PunishmentType
import net.minepact.api.punishment.modifier.AnnouncementModifier
import net.minepact.api.punishment.modifier.ScopeModifier
import net.minepact.core.discord.embeds.punishments.banEmbed
import net.minepact.core.discord.embeds.punishments.kickEmbed
import net.minepact.core.global.commands.punishment.helper.createPunishment
import net.minepact.core.global.commands.punishment.helper.extractRawTokens
import net.minepact.core.global.commands.punishment.helper.message.getPunishmentBroadcast
import net.minepact.core.global.commands.punishment.helper.message.getPunishmentMessage
import net.minepact.core.global.commands.punishment.helper.parseLength
import net.minepact.core.global.commands.punishment.helper.parseReason
import net.minepact.core.global.commands.punishment.helper.resolveAnnouncementModifier
import net.minepact.core.global.commands.punishment.helper.resolveScopeModifier
import net.minepact.core.global.commands.punishment.helper.retrieveModifiers
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerKickEvent

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
    permission = "minepact.punishment.kick"
){
    override fun execute(
        sender: CommandSender,
        args: MutableList<Argument<*>>
    ): Result {
        val targetName: String = args[0].value as String
        val target: Player? = Bukkit.getPlayer(targetName)

        val rawTokens: MutableList<String> = extractRawTokens(args)
        val modifiers = retrieveModifiers(rawTokens)

        val reason = parseReason(rawTokens)

        val scope: ScopeModifier = resolveScopeModifier(modifiers)
        val announcement: AnnouncementModifier = resolveAnnouncementModifier(modifiers)

        val punishment = createPunishment(
            sender = sender,
            type = PunishmentType.KICK,
            targetName = targetName,
            reason = reason,
            expiresAt = Long.MIN_VALUE,
            scope = scope
        )

        PunishmentRepository.insert(punishment)
        val targetMessage: String = getPunishmentMessage(punishment, announcement)
        val broadcastMessage: String = getPunishmentBroadcast(punishment, announcement)

        target?.kick(MiniMessage.miniMessage().deserialize(targetMessage), PlayerKickEvent.Cause.KICK_COMMAND)
        when (announcement) {
            AnnouncementModifier.PUBLIC -> Bukkit.getOnlinePlayers().forEach { it.send(broadcastMessage) }
            AnnouncementModifier.SILENT -> Bukkit.getOnlinePlayers().forEach { if (it.hasPermission("minepact.punish.notify")) it.send(broadcastMessage) }
        }

        Main.PUNISHMENTS_WEBHOOK.sendMessage("", listOf(kickEmbed(punishment, listOf(scope, announcement))))
        return Result.SUCCESS
    }
}