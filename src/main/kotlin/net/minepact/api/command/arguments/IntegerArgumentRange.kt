package net.minepact.api.command.arguments

class IntegerArgumentRange(
    override var min: Int = Int.MIN_VALUE,
    override var max: Int = Int.MAX_VALUE
) : ArgumentRange<Int>