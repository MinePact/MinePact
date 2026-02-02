package net.minepact.api.command.arguments

import org.bukkit.command.CommandSender

class ExpectedArgument(
    val potentialValues: List<String>,
    var identifier: List<String> = potentialValues.map {
        "<grey><italics><$it></italics></grey>"
    },
    var inputType: ArgumentInputType = ArgumentInputType.STRING,
    var optional: Boolean = false,
    var range: ArgumentRange<*> = EmptyArgumentRange(),

    val permission: String? = null,
    val senderFilter: ((CommandSender) -> Boolean)? = null,
    val dynamicProvider: ((CommandSender) -> List<String>)? = null
) {}