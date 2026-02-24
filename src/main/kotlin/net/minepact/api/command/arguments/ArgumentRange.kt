package net.minepact.api.command.arguments

/**
 * Represents a range of numbers for command arguments.
 *
 * @param T The type of number (e.g., Int, Double) that the range represents.
 *
 * @property min The minimum value of the range.
 * @property max The maximum value of the range.
 *
 * @see IntegerArgumentRange
 * @see DoubleArgumentRange
 * @see FloatArgumentRange
 * @see EmptyArgumentRange
 *
 * @author dankenyon - 22/02/26
 */
sealed interface ArgumentRange<T : Number> {
    val min: T
    val max: T
}