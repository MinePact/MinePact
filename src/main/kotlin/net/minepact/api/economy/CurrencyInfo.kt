package net.minepact.api.economy

import net.minepact.api.server.ServerType

abstract class CurrencyInfo(
    val server: ServerType

) {
    abstract val name: String
    abstract val symbol: String
}