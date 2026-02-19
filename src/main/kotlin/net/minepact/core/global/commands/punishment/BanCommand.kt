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
import net.minepact.api.config.ConfigurationRegistry
import net.minepact.api.messages.send
import net.minepact.api.misc.formatDate
import net.minepact.api.misc.formatDuration
import net.minepact.api.misc.getLengthFromIdentifier
import net.minepact.api.punishment.Punishment
import net.minepact.api.punishment.PunishmentModifiers
import net.minepact.api.punishment.PunishmentType
import net.minepact.api.server.ServerInfo
import net.minepact.core.discord.embeds.punishments.banEmbed
import net.minepact.core.discord.embeds.punishments.muteEmbed
import net.minepact.core.global.commands.punishment.helper.createPunishment
import net.minepact.core.global.commands.punishment.helper.extractRawTokens
import net.minepact.core.global.commands.punishment.helper.message.getPunishmentBroadcast
import net.minepact.core.global.commands.punishment.helper.message.getPunishmentMessage
import net.minepact.core.global.commands.punishment.helper.parseLength
import net.minepact.core.global.commands.punishment.helper.parseReason
import net.minepact.core.global.commands.punishment.helper.resolveAnnouncementModifier
import net.minepact.core.global.commands.punishment.helper.resolveScopeModifier
import net.minepact.core.global.commands.punishment.helper.retrieveModifiers
import net.minepact.core.global.configs.PunishmentConfig
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerKickEvent
import java.util.UUID
import kotlin.String

@Suppress("unused")
class BanCommand : Command( // /ban <player> 1d Ban Evasion -s
    name = "ban",
    description = "Bans a player from the server.",
    permission = "minepact.punishments.ban",
    aliases = mutableListOf(),
    usage = CommandUsage(
        label = "ban", arguments = listOf(
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

        val scope = resolveScopeModifier(modifiers)
        val announcement = resolveAnnouncementModifier(modifiers)

        val punishment = createPunishment(
            sender = sender,
            type = PunishmentType.BAN,
            targetName = targetName,
            reason = reason,
            expiresAt = expiresAt,
            scope = scope
        )

        Main.PUNISHMENT_REPOSITORY.insert(punishment)
        val targetMessage: String = getPunishmentMessage(punishment, announcement)
        val broadcastMessage: String = getPunishmentBroadcast(punishment, announcement)

        target?.kick(MiniMessage.miniMessage().deserialize(targetMessage), PlayerKickEvent.Cause.BANNED)

        if (announcement == PunishmentModifiers.PUBLIC) {
            Bukkit.getOnlinePlayers().forEach { it.send(broadcastMessage) }
        } else if (announcement == PunishmentModifiers.SILENT) {
            Bukkit.getOnlinePlayers().forEach { if (it.hasPermission("minepact.punish.notify")) it.send(broadcastMessage) }
        }

        Main.PUNISHMENTS_WEBHOOK.sendMessage("", listOf(banEmbed(punishment, listOf(scope, announcement))))
        return Result.SUCCESS
    }
}