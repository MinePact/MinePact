package net.minepact.api.math.helper.rational

import net.minepact.api.math.Rational

operator fun Long.plus(r: Rational) = Rational.Companion(this) + r
operator fun Long.minus(r: Rational) = Rational.Companion(this) - r
operator fun Long.times(r: Rational) = Rational.Companion(this) * r
operator fun Long.div(r: Rational) = Rational.Companion(this) / r
