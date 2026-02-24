package net.minepact.api.command.arguments

/**
 * An implementation of [ArgumentRange] that represents an empty range, where the minimum and maximum values are both set to 0.
 *
 * @see ArgumentRange
 *
 * @author dankenyon - 22/02/26
 */
class EmptyArgumentRange(
    override val min: Number = 0,
    override val max: Number = 0
) : ArgumentRange<Number>