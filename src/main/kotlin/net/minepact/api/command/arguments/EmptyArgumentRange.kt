package net.minepact.api.command.arguments

class EmptyArgumentRange(
    override val min: Number = 0,
    override val max: Number = 0
) : ArgumentRange<Number>