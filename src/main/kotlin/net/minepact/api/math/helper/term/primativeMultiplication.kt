package net.minepact.api.math.helper.term

import net.minepact.api.math.Rational
import net.minepact.api.math.Term

operator fun Int.times(term: Term): Term = term * this
operator fun Long.times(term: Term): Term = term * this
operator fun Rational.times(term: Term): Term = term * this