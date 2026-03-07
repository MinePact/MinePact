package net.minepact.api.math.helper.symbol

import net.minepact.api.math.Rational
import net.minepact.api.math.Symbol
import net.minepact.api.math.Term

operator fun Int.times(symbol: Symbol): Term = Term(Rational(this), mapOf(symbol to 1))
operator fun Long.times(symbol: Symbol): Term = Term(Rational(this), mapOf(symbol to 1))
operator fun Rational.times(symbol: Symbol): Term = Term(this, mapOf(symbol to 1))