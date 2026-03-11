command(
    server = ServerType.GLOBAL,
    name = "quit",
    description = "test",
    aliases = mutableListOf("q", "leave"),
    permission = Permission("minepact.admin.leave"),
    usage = CommandUsage(label = "quit", arguments = listOf(
        ExpectedArgument(name = "ex", potentialValues = listOf("am", "pl", "e"))
    )),
    cooldown = 0.5,
    log = true,
) { sender: net.minepact.api.player.Player, args: MutableList<Argument<*>> ->
    sender.asPlayer()!!.kick(MiniMessage.miniMessage().deserialize("<red>You have been kicked a second time!"))
    net.minepact.api.command.Result.SUCCESS
}