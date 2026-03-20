package net.minepact.api.command.arguments

import net.minepact.api.command.Provider

object Arguments {
    val PLAYERS_REQUIRED: ExpectedArgument = ExpectedArgument(
        name = "players",
        inputType = ArgumentInputType.PLAYER,
        dynamicProvider = Provider.PLAYERS,
        optional = false,
    )
    val PLAYERS_OPTIONAL: ExpectedArgument = ExpectedArgument(
        name = "players",
        inputType = ArgumentInputType.PLAYER,
        dynamicProvider = Provider.PLAYERS,
        optional = true,
    )
}