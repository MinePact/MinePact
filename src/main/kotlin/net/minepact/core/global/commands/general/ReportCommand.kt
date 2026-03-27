package net.minepact.core.global.commands.general

import net.minepact.Main
import net.minepact.Main.Companion.SERVER
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Arguments
import net.minepact.api.command.dsl.Command
import net.minepact.api.discord.Webhooks
import net.minepact.api.discord.embed.Author
import net.minepact.api.discord.embed.Embed
import net.minepact.api.discord.embed.Field
import net.minepact.api.discord.embed.Footer
import net.minepact.api.messages.helper.msg
import net.minepact.api.permissions.Permission
import net.minepact.api.player.Player
import net.minepact.api.player.PlayerRegistry
import java.text.SimpleDateFormat
import java.util.Date

class ReportCommand : Command() {
    init {
        command("report") {
            description = "Report a player for misconduct"
            permission = Permission("minepact.report")

            argument(Arguments.PLAYERS_REQUIRED) {
                argument("reason", optional = false, consumeRemaining = true) {
                    executes { sender, args ->
                        val target: Player = args[0].value as Player
                        val reasonArg = args.getOrNull(1)
                        val reasonParts = reasonArg?.value as? List<*>
                        val reason = reasonParts?.joinToString(separator = " ") ?: ""

                        PlayerRegistry.online().filter { it.hasPermission(Permission("minepact.report.see", true)) }.forEach {
                            it.sendMessage(msg {
                                +"<red><bold>REPORT <grey>| <white>${sender.data.name} <grey>reported <white>${target.data.name} <grey>for: <white>$reason"
                            })
                        }

                        Webhooks.PUNISHMENTS_WEBHOOK.sendMessage(embeds = listOf(Embed(
                            author = Author(),
                            title = "Player Report",
                            url = null,
                            description = "[${SERVER.info.name}] ${target.data.name} was reported by ${sender.data.name}",
                            colour = 0xFF0000,
                            fields = listOf(
                                Field("Reason", reason, false),
                            ),
                            thumbnail = null,
                            image = null,
                            footer = Footer("Server UUID: ${SERVER.info.uuid} | Time: ${SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
                                .format(Date(Main.SERVER_START_TIME))}")
                        )))
                        Result.SUCCESS
                    }
                }
            }
        }
    }
}