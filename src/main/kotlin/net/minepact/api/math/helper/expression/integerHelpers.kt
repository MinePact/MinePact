package net.minepact.api.math.helper.expression

import net.minepact.api.math.Expression

operator fun Int.plus(e: Expression): Expression = Expression.constant(this) + e
operator fun Int.minus(e: Expression): Expression = Expression.constant(this) - e
operator fun Int.times(e: Expression): Expression = e * this