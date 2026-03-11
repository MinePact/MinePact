package net.minepact.core.global.commands.staff

import net.minepact.Main
import net.minepact.api.command.Command
import net.minepact.api.command.CommandUsage
import net.minepact.api.command.Provider
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Argument
import net.minepact.api.command.arguments.ExpectedArgument
import net.minepact.api.messages.Message
import net.minepact.api.messages.MessageBuilder
import net.minepact.api.player.Player
import net.minepact.api.player.PlayerRegistry
import net.minepact.api.permissions.Permission

class StaffChatCommand : Command(
    name = "staffchat",
    description = "Sends a message to all staff on the server.",
    permission = Permission("minepact.staff-chat"),
    aliases = mutableListOf("sc", "staff-chat"),
    usage = CommandUsage(label = "staffchat", arguments = listOf(
        ExpectedArgument("message", dynamicProvider = Provider.EMPTY, optional = false)
    ))
) {
    override fun execute(
        sender: Player,
        args: MutableList<Argument<*>>
    ): Result {
        val contents: String = args.joinToString(separator = " ") { it.value as String }
        val targets: List<Player> = PlayerRegistry.online()
            .filter { it.hasPermission(Permission("minepact.staff-chat")) }
        val LEFT_SQUARE_BRACE = "<hex:3ABDFF><bold>[</bold></hex:3ABDFF>"
        val RIGHT_SQUARE_BRACE = "<hex:3ABDFF><bold>]</bold></hex:3ABDFF>"

        val msg: Message = MessageBuilder()
            .append(LEFT_SQUARE_BRACE).append($$"<white>${SERVER_NAME}</white>").append(RIGHT_SQUARE_BRACE).append(" ")
            .append(LEFT_SQUARE_BRACE).append($$"<white>${CHAT_TYPE_NAME}</white>").append(RIGHT_SQUARE_BRACE).append(" ")
            .append(LEFT_SQUARE_BRACE).append($$"<white>${PLAYER_RANK}</white>").append(RIGHT_SQUARE_BRACE).append(" ")
            .append($$"<white><bold>${PLAYER_NAME}</bold>:</white>").append(" ")
            .append($$"<white>${MESSAGE_CONTENTS}</white>").append(" ")
            .append(
                    "<click:copy:${contents}><hover:'<red>Click to copy!'>" +
                    "<gray>[Copy]</gray>" +
                    "</hover></click>"
            )
            .replace(
                Pair($$"${SERVER_NAME}", Main.SERVER.info.name),
                Pair($$"${CHAT_TYPE_NAME}", "Staff Chat"),
                Pair($$"${PLAYER_RANK}", sender.getPrimaryGroup()?.displayName ?: "Member"),
                Pair($$"${PLAYER_NAME}", sender.data.name),
                Pair($$"${MESSAGE_CONTENTS}", contents)
            )
            .build()

        targets.forEach { it.sendMessage(msg) }
        return Result.SUCCESS
    }

}