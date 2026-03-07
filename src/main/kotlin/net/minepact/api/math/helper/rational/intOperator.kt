package net.minepact.api.math.helper.rational

import net.minepact.api.math.Rational

operator fun Int.plus(r: Rational) = Rational.Companion(this) + r
operator fun Int.minus(r: Rational) = Rational.Companion(this) - r
operator fun Int.times(r: Rational) = Rational.Companion(this) * r
operator fun Int.div(r: Rational) = Rational.Companion(this) / r
