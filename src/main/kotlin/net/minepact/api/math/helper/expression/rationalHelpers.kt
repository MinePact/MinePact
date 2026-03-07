package net.minepact.api.math.helper.expression

import net.minepact.api.math.Expression
import net.minepact.api.math.Rational

operator fun Rational.plus(e: Expression): Expression = Expression.constant(this) + e
operator fun Rational.minus(e: Expression): Expression = Expression.constant(this) - e
operator fun Rational.times(e: Expression): Expression = e * this