package net.minepact.api.command.arguments

/**
 * Represents a range of float values for command arguments.
 *
 * @property min The minimum value of the range (inclusive).
 * @property max The maximum value of the range (inclusive).
 *
 * @see ArgumentRange
 *
 * @author dankenyon - 22/02/26
 */
class FloatArgumentRange(
    override var min: Float = Float.MIN_VALUE,
    override var max: Float = Float.MAX_VALUE
) : ArgumentRange<Float>