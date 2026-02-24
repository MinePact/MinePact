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
import net.minepact.api.messages.send
import net.minepact.api.player.Player
import net.minepact.api.player.PlayerRegistry
import net.minepact.api.player.permissions.Permission
import net.minepact.api.punishment.PunishmentType
import net.minepact.api.punishment.modifier.AnnouncementModifier
import net.minepact.api.punishment.modifier.ScopeModifier
import net.minepact.core.discord.embeds.punishments.unmuteEmbed
import net.minepact.core.global.commands.staff.punishment.helper.extractRawTokens
import net.minepact.core.global.commands.staff.punishment.helper.message.getRevokalMessage
import net.minepact.core.global.commands.staff.punishment.helper.parseReason
import net.minepact.core.global.commands.staff.punishment.helper.resolveAnnouncementModifier
import net.minepact.core.global.commands.staff.punishment.helper.retrieveModifiers
import net.minepact.core.global.commands.staff.punishment.helper.revertPunishment
import org.bukkit.Bukkit

class UnmuteCommand : Command(
    name = "unmute",
    description = "Unmute a player",
    usage = CommandUsage(label = "unmute", arguments = listOf(
        ExpectedArgument(name = "player", dynamicProvider = Provider.PLAYERS, inputType = ArgumentInputType.STRING),
        ExpectedArgument(name = "reason", inputType = ArgumentInputType.STRING, optional = true),
        ExpectedArgument(name = "modifiers", dynamicProvider = Provider.EMPTY, optional = true)
    )),
    aliases = mutableListOf(""),
    permission = Permission("minepact.punishments.unmute"),
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

        PunishmentRepository.findActiveByTargetAndType(PlayerRegistry.get(targetName).get().data.uuid, PunishmentType.MUTE).thenAccept { mute ->
            if (mute == null) {
                sender.sendMessage("<red>No active mute found for $targetName")
                return@thenAccept
            }

            val reverted = revertPunishment(mute, sender.data.uuid, reason)
            PunishmentRepository.insert(reverted)

            when (announcement) {
                AnnouncementModifier.PUBLIC -> Bukkit.getOnlinePlayers().forEach { it.send(getRevokalMessage(mute, AnnouncementModifier.PUBLIC)) }
                AnnouncementModifier.SILENT -> Bukkit.getOnlinePlayers().forEach { if (it.hasPermission("minepact.punish.notify")) it.send(getRevokalMessage(mute, AnnouncementModifier.PUBLIC)) }
            }

            Main.PUNISHMENTS_WEBHOOK.sendMessage("", listOf(unmuteEmbed(mute, listOf(ScopeModifier.GLOBAL, announcement))))
        }

        return Result.SUCCESS
    }
}