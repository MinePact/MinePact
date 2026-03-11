package net.minepact.core.global.commands.staff.punishment

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
import net.minepact.api.player.PlayerRegistry
import net.minepact.api.permissions.Permission
import net.minepact.api.punishment.PunishmentType
import net.minepact.api.punishment.modifier.AnnouncementModifier
import net.minepact.api.punishment.modifier.ScopeModifier
import net.minepact.core.discord.embeds.punishments.ipbanEmbed
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
import kotlin.String

class IpBanCommand : Command(
    name = "ipban",
    description = "Bans a player and their ip from the server.",
    permission = Permission("minepact.punishments.ban-ip"),
    aliases = mutableListOf(),
    usage = CommandUsage(
        label = "ipban", arguments = listOf(
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
        sender: net.minepact.api.player.Player,
        args: MutableList<Argument<*>>
    ): Result {
        val targetName: String = args[0].value as String
        val target: net.minepact.api.player.Player = PlayerRegistry.get(targetName).get()


        val rawTokens: MutableList<String> = extractRawTokens(args)
        val modifiers = retrieveModifiers(rawTokens)

        val (_, expiresAt) = parseLength(rawTokens)
        val reason = parseReason(rawTokens)

        val scope: ScopeModifier = resolveScopeModifier(modifiers)
        val announcement: AnnouncementModifier = resolveAnnouncementModifier(modifiers)

        val punishment = createPunishment(
            player = sender,
            type = PunishmentType.IP_BAN,
            target = target.data.uuid,
            reason = reason,
            expiresAt = expiresAt,
            scope = scope
        )

        PunishmentRepository.findActiveByTargetAndType(target.data.uuid, PunishmentType.IP_BAN).thenAccept { activeBan ->
            if (activeBan != null && activeBan.expiresAt >= expiresAt && expiresAt != Long.MIN_VALUE) {
                sender.sendMessage("<white>$targetName <red>is already banned. Their current ban expires at <white>${formatDate(activeBan.expiresAt)}<red>.")
                return@thenAccept
            }

            if (activeBan != null && activeBan.expiresAt < expiresAt) {
                val reverted = revertPunishment(activeBan, sender.data.uuid, "OTHER_IP-BAN_OVERRIDE")
                PunishmentRepository.insert(reverted)
            }

            PunishmentRepository.insert(punishment)
            val broadcastMessage: String = getPunishmentBroadcast(punishment, announcement)

            Bukkit.getScheduler().runTask(Main.instance) { _ ->
                target.asPlayer()?.kick(MiniMessage.miniMessage().deserialize(getPunishmentMessage(punishment, announcement)), PlayerKickEvent.Cause.BANNED)
            }
            when (announcement) {
                AnnouncementModifier.PUBLIC -> Bukkit.getOnlinePlayers().forEach { it.send(broadcastMessage) }
                AnnouncementModifier.SILENT -> Bukkit.getOnlinePlayers().forEach { if (it.hasPermission("minepact.punish.notify")) it.send(broadcastMessage) }
            }

            Main.PUNISHMENTS_WEBHOOK.sendMessage("", listOf(ipbanEmbed(punishment, listOf(scope, announcement))))
        }

        return Result.SUCCESS
    }
}