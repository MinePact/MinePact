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
import net.minepact.api.server.Server
import net.minepact.api.server.ServerInfo
import net.minepact.core.discord.embeds.punishments.banEmbed
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
        label = "restart", arguments = listOf(
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
        val messageLines = ConfigurationRegistry.get(PunishmentConfig::class).ban.kickMessage.toString().replace("[", "").replace("]", "").replace(", ", "\n")

        val playerName: String = args[0].value as String
        val rawTokens: MutableList<String> = if (args.size > 1) args.drop(1).map { it.value as String }.toMutableList() else mutableListOf()

        val modifiers: MutableList<PunishmentModifiers> = mutableListOf()
        val tokenIterator = rawTokens.listIterator()
        while (tokenIterator.hasNext()) {
            val token = tokenIterator.next()
            val found = PunishmentModifiers.entries.firstOrNull { mod -> mod.possibleIdentifiers.contains(token) }
            if (found != null) {
                modifiers.add(found)
                tokenIterator.remove()
            }
        }

        var lengthToken: String? = null
        val lengthIndex = rawTokens.indexOfFirst { tok ->
                    tok.equals("permanent", ignoreCase = true) ||
                    tok.matches(Regex("^\\d+[smhdwy]$"))
        }
        if (lengthIndex >= 0) lengthToken = rawTokens.removeAt(lengthIndex)
        val length: String = lengthToken ?: "permanent"
        val expiresAt: Long = if (length.equals("permanent", ignoreCase = true)) Long.MIN_VALUE else try {
            System.currentTimeMillis() + getLengthFromIdentifier(length)
        } catch (_: Exception) { Long.MIN_VALUE }

        val reason: String = if (rawTokens.isNotEmpty()) rawTokens.joinToString(" ") else "No reason provided."

        val punishmentScope: PunishmentModifiers = if (modifiers.contains(PunishmentModifiers.GLOBAL) && modifiers.contains(PunishmentModifiers.LOCAL))
            PunishmentModifiers.valueOf(Main.MAIN_CONFIG.default_punishment_scope_modifier)
        else if (modifiers.contains(PunishmentModifiers.GLOBAL)) PunishmentModifiers.GLOBAL
        else if (modifiers.contains(PunishmentModifiers.LOCAL)) PunishmentModifiers.LOCAL
        else PunishmentModifiers.valueOf(Main.MAIN_CONFIG.default_punishment_scope_modifier)

        val announcementStatus: PunishmentModifiers = if (modifiers.contains(PunishmentModifiers.SILENT) && modifiers.contains(PunishmentModifiers.PUBLIC))
            PunishmentModifiers.valueOf(Main.MAIN_CONFIG.default_announcement_status_modifier)
        else if (modifiers.contains(PunishmentModifiers.SILENT)) PunishmentModifiers.SILENT
        else if (modifiers.contains(PunishmentModifiers.PUBLIC)) PunishmentModifiers.PUBLIC
        else PunishmentModifiers.valueOf(Main.MAIN_CONFIG.default_announcement_status_modifier)

        val servers: List<ServerInfo> = if (punishmentScope == PunishmentModifiers.GLOBAL) Main.SERVER_REPOSITORY.findAll().get()
                                        else listOf(Main.SERVER.info)
        val serverUUIDs: List<UUID> = servers.map { it.uuid }
        val punishment = Punishment(
            id = Punishment.generateId(),
            targetServers = serverUUIDs,
            type = PunishmentType.BAN ,
            targetName = playerName,
            issuerName = sender.name,
            reason = reason,
            punishedAt = System.currentTimeMillis(),
            expiresAt = expiresAt
        )

        Main.PUNISHMENT_REPOSITORY.insert(punishment)
        val target: Player? = Bukkit.getPlayer(playerName)
        target?.kick(MiniMessage.miniMessage().deserialize(messageLines
            .replace("{REASON}", punishment.reason).replace("{EXPIRES_AT}", formatDate(punishment.expiresAt))
            .replace("{EXPIRES_IN}", formatDuration(punishment.expiresAt - System.currentTimeMillis()))
            .replace("{TARGET}", punishment.targetName).replace("{ISSUER}", punishment.issuerName)
        ), PlayerKickEvent.Cause.BANNED)

        Main.PUNISHMENTS_WEBHOOK.sendMessage(embeds = listOf(banEmbed(punishment, listOf(punishmentScope, announcementStatus))))

        return Result.SUCCESS
    }
}