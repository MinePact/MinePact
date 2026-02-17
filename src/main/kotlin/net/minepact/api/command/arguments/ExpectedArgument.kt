package net.minepact.api.command.arguments

import org.bukkit.command.CommandSender

class ExpectedArgument(
    val name: String,
    var potentialValues: List<String>? = null,
    var inputType: ArgumentInputType = ArgumentInputType.STRING,
    var optional: Boolean = false,
    var range: ArgumentRange<*> = EmptyArgumentRange(),

    val permission: String? = null,
    val senderFilter: ((CommandSender) -> Boolean)? = null,
    val dynamicProvider: ((CommandSender) -> List<String>)? = null
)