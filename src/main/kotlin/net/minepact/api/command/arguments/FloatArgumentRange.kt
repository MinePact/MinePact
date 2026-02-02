package net.minepact.api.command.arguments

class FloatArgumentRange(
    override var min: Float = Float.MIN_VALUE,
    override var max: Float = Float.MAX_VALUE
) : ArgumentRange<Float>