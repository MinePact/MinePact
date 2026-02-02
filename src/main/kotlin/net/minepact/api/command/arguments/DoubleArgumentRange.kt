package net.minepact.api.command.arguments

class DoubleArgumentRange(
    override var min: Double = Double.MIN_VALUE,
    override var max: Double = Double.MAX_VALUE
) : ArgumentRange<Double>