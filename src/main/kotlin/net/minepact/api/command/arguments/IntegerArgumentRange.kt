package net.minepact.api.command.arguments

/**
 * Represents a range of integer values for command arguments.
 *
 * @property min The minimum value of the range (inclusive).
 * @property max The maximum value of the range (inclusive).
 *
 * @see ArgumentRange
 *
 * @author dankenyon - 22/02/26
 */
class IntegerArgumentRange(
    override var min: Int = Int.MIN_VALUE,
    override var max: Int = Int.MAX_VALUE
) : ArgumentRange<Int>