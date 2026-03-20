package net.minepact.api.command.arguments

import org.bukkit.command.CommandSender

/**
 * The argument that the command requires to function. This is used for tab completion and command usage.
 *
 * @property name The name of the argument.
 * @property potentialValues The all values for this argument.
 * @property inputType The type of input for this argument.
 * @property optional Whether this argument is optional or not.
 * @property range The range of values for this argument.
 * @property permission The permission required to use this argument.
 * @property senderFilter Whether the sender of the command can use this argument.
 * @property dynamicProvider A provider for potential values that is generated dynamically based on the sender of the command.
 *
 * @author dankenyon - 22/02/26
 */
open class ExpectedArgument(
    val name: String,
    var potentialValues: List<String>? = null,
    var inputType: ArgumentInputType = ArgumentInputType.STRING,
    var optional: Boolean = false,
    var range: ArgumentRange<*> = EmptyArgumentRange(),

    val permission: String? = null,
    val senderFilter: ((CommandSender) -> Boolean)? = null,
    val dynamicProvider: ((CommandSender) -> List<String>)? = null
)
