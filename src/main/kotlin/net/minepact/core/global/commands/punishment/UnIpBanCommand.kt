package net.minepact.core.global.commands.punishment

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
import net.minepact.core.discord.embeds.punishments.kickEmbed
import net.minepact.core.discord.embeds.punishments.unbanEmbed
import net.minepact.core.discord.embeds.punishments.unipbanEmbed
import net.minepact.core.global.commands.punishment.helper.extractRawTokens
import net.minepact.core.global.commands.punishment.helper.message.getRevokalMessage
import net.minepact.core.global.commands.punishment.helper.parseReason
import net.minepact.core.global.commands.punishment.helper.resolveAnnouncementModifier
import net.minepact.core.global.commands.punishment.helper.retrieveModifiers
import net.minepact.core.global.commands.punishment.helper.revertPunishment
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

class UnIpBanCommand : Command(
    name = "un-ipban",
    description = "",
    usage = CommandUsage(label = "un-ipban", arguments = listOf(
        ExpectedArgument(name = "player", dynamicProvider = Provider.PLAYERS, inputType = ArgumentInputType.STRING),
        ExpectedArgument(name = "reason", inputType = ArgumentInputType.STRING, optional = true),
        ExpectedArgument(name = "modifiers", dynamicProvider = Provider.EMPTY, optional = true)
    )),
    aliases = mutableListOf(""),
    permission = "minepact.punishments.unban",
    cooldown = 1.0,
) {
    override fun execute(
        sender: CommandSender,
        args: MutableList<Argument<*>>
    ): Result {
        val targetName: String = args[0].value as String
        val rawTokens: MutableList<String> = extractRawTokens(args)
        val modifiers = retrieveModifiers(rawTokens)
        val reason = parseReason(rawTokens)
        val announcement: AnnouncementModifier = resolveAnnouncementModifier(modifiers)

        PunishmentRepository.findActiveByTargetAndType(targetName, PunishmentType.IP_BAN).thenAccept { ban ->
            if (ban == null) {
                sender.send("<red>No active ip-ban found for <white>$targetName")
                return@thenAccept
            }

            val reverted = revertPunishment(ban, sender.name, reason)
            PunishmentRepository.insert(reverted)

            when (announcement) {
                AnnouncementModifier.PUBLIC -> Bukkit.getOnlinePlayers().forEach { it.send(getRevokalMessage(ban, AnnouncementModifier.PUBLIC)) }
                AnnouncementModifier.SILENT -> Bukkit.getOnlinePlayers().forEach { if (it.hasPermission("minepact.punish.notify")) it.send(getRevokalMessage(ban, AnnouncementModifier.PUBLIC)) }
            }

            Main.PUNISHMENTS_WEBHOOK.sendMessage("", listOf(unipbanEmbed(ban, listOf(ScopeModifier.GLOBAL, announcement))))
        }

        return Result.SUCCESS
    }
}