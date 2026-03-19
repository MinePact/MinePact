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
import net.minepact.core.discord.embeds.punishments.unbanEmbed
import net.minepact.core.global.commands.staff.punishment.helper.extractRawTokens
import net.minepact.core.global.commands.staff.punishment.helper.message.getRevokalMessage
import net.minepact.core.global.commands.staff.punishment.helper.parseReason
import net.minepact.core.global.commands.staff.punishment.helper.resolveAnnouncementModifier
import net.minepact.core.global.commands.staff.punishment.helper.retrieveModifiers
import net.minepact.core.global.commands.staff.punishment.helper.revertPunishment
import org.bukkit.Bukkit

class UnbanCommand : Command(
    name = "unban",
    description = "Unban a player",
    usage = CommandUsage(label = "unban", arguments = listOf(
        ExpectedArgument(name = "player", dynamicProvider = Provider.PLAYERS, inputType = ArgumentInputType.STRING),
        ExpectedArgument(name = "reason", inputType = ArgumentInputType.STRING, optional = true),
        ExpectedArgument(name = "modifiers", dynamicProvider = Provider.EMPTY, optional = true)
    )),
    aliases = mutableListOf(""),
    permission = Permission("minepact.punishments.unban"),
    cooldown = 1.0,
) {
    override fun execute(
        sender: Player,
        args: MutableList<Argument<*>>
    ): Result {
        val targetName: String = args[0].value as String
        val rawTokens: MutableList<String> = extractRawTokens(args)
        val modifiers = retrieveModifiers(rawTokens)
        val reason = parseReason(rawTokens)
        val announcement: AnnouncementModifier = resolveAnnouncementModifier(modifiers)

        PunishmentRepository.findActiveByTargetAndType(PlayerRegistry.get(targetName).get().data.uuid, PunishmentType.BAN).thenAccept { ban ->
            if (ban == null) {
                sender.sendMessage("<red>No active ban found for <white>$targetName")
                return@thenAccept
            }

            val reverted = revertPunishment(ban, sender.data.uuid, reason)
            PunishmentRepository.insert(reverted)

            when (announcement) {
                AnnouncementModifier.PUBLIC -> Bukkit.getOnlinePlayers().forEach { it.send(getRevokalMessage(ban, AnnouncementModifier.PUBLIC)) }
                AnnouncementModifier.SILENT -> Bukkit.getOnlinePlayers().forEach { if (it.hasPermission("minepact.punish.notify")) it.send(getRevokalMessage(ban, AnnouncementModifier.PUBLIC)) }
            }

            PUNISHMENTS_WEBHOOK.sendMessage("", listOf(unbanEmbed(ban, listOf(ScopeModifier.GLOBAL, announcement))))
        }

        return Result.SUCCESS
    }
}