package net.minepact.api.command.arguments

/**
 * An implementation of [ArgumentRange] for [Double] values.
 *
 * @property min The minimum value of the range. Default is [Double.MIN_VALUE].
 * @property max The maximum value of the range. Default is [Double.MAX_VALUE].
 *
 * @see ArgumentRange
 *
 * @author dankenyon - 22/02/26
 */
class DoubleArgumentRange(
    override var min: Double = Double.MIN_VALUE,
    override var max: Double = Double.MAX_VALUE
) : ArgumentRange<Double>