package net.minepact.api.command.arguments

/**
 * Represents a parsed argument from a command input.
 *
 * @param T The type of the argument's value.
 * @property raw The original string input for this argument.
 * @property value The parsed value of the argument, of type T.
 * @property type The type of the argument input, used for parsing and validation.
 *
 * @see ArgumentInputType
 *
 * @author dankenyon - 22/02/26
 */
class Argument<T : Any>(
    val raw: String,
    val value: T,
    val type: ArgumentInputType,
)