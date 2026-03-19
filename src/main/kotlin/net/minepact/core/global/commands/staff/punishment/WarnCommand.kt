package net.minepact.core.global.commands.staff.punishment

import net.minepact.Main
import net.minepact.api.command.Command
import net.minepact.api.command.CommandUsage
import net.minepact.api.command.Provider
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Argument
import net.minepact.api.command.arguments.ArgumentInputType
import net.minepact.api.command.arguments.ExpectedArgument
import net.minepact.api.data.repository.PunishmentRepository
import net.minepact.api.discord.Webhooks.PUNISHMENTS_WEBHOOK
import net.minepact.api.messages.send
import net.minepact.api.player.Player
import net.minepact.api.player.PlayerRegistry
import net.minepact.api.permissions.Permission
import net.minepact.api.punishment.PunishmentType
import net.minepact.api.punishment.modifier.AnnouncementModifier
import net.minepact.api.punishment.modifier.ScopeModifier
import net.minepact.core.discord.embeds.punishments.warnEmbed
import net.minepact.core.global.commands.staff.punishment.helper.createPunishment
import net.minepact.core.global.commands.staff.punishment.helper.extractRawTokens
import net.minepact.core.global.commands.staff.punishment.helper.message.getPunishmentBroadcast
import net.minepact.core.global.commands.staff.punishment.helper.message.getPunishmentMessage
import net.minepact.core.global.commands.staff.punishment.helper.parseLength
import net.minepact.core.global.commands.staff.punishment.helper.parseReason
import net.minepact.core.global.commands.staff.punishment.helper.resolveAnnouncementModifier
import net.minepact.core.global.commands.staff.punishment.helper.resolveScopeModifier
import net.minepact.core.global.commands.staff.punishment.helper.retrieveModifiers
import org.bukkit.Bukkit

class WarnCommand : Command(
    name = "warn",
    description = "Warn a player from the server.",
    permission = Permission("minepact.punishments.warn"),
    aliases = mutableListOf(),
    usage = CommandUsage(
        label = "warn", arguments = listOf(
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
        sender: Player,
        args: MutableList<Argument<*>>
    ): Result {
        val targetName: String = args[0].value as String
        val target: Player = PlayerRegistry.get(targetName).get()
        val rawTokens: MutableList<String> = extractRawTokens(args)
        val modifiers = retrieveModifiers(rawTokens)

        val (_, parsedExpiresAt) = parseLength(rawTokens)
        val expiresAt = if (parsedExpiresAt == Long.MIN_VALUE) System.currentTimeMillis() + 604_800_000
        else parsedExpiresAt

        val reason = parseReason(rawTokens)
        val scope: ScopeModifier = resolveScopeModifier(modifiers)
        val announcement: AnnouncementModifier = resolveAnnouncementModifier(modifiers)

        val punishment = createPunishment(
            player = sender,
            type = PunishmentType.WARN,
            target = target.data.uuid,
            reason = reason,
            expiresAt = expiresAt,
            scope = scope
        )

        PunishmentRepository.insert(punishment)
        val broadcastMessage: String = getPunishmentBroadcast(punishment, announcement)

        target.sendMessage(getPunishmentMessage(punishment, announcement))
        when (announcement) {
            AnnouncementModifier.PUBLIC -> Bukkit.getOnlinePlayers().forEach { it.send(broadcastMessage) }
            AnnouncementModifier.SILENT -> Bukkit.getOnlinePlayers().forEach { if (it.hasPermission("minepact.punish.notify")) it.send(broadcastMessage) }
        }

        PUNISHMENTS_WEBHOOK.sendMessage("", listOf(warnEmbed(punishment, listOf(scope, announcement))))
        return Result.SUCCESS
    }
}