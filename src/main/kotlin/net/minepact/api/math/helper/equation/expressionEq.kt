package net.minepact.api.math.helper.equation

import net.minepact.api.math.Equation
import net.minepact.api.math.Expression
import net.minepact.api.math.Rational

@Suppress("EXTENSION_SHADOWED_BY_MEMBER") infix fun Expression.eq(rhs: Expression): Equation = Equation(this, rhs)
@Suppress("EXTENSION_SHADOWED_BY_MEMBER") infix fun Expression.eq(rhs: Int): Equation = Equation(this, Expression.constant(rhs))
@Suppress("EXTENSION_SHADOWED_BY_MEMBER") infix fun Expression.eq(rhs: Rational): Equation = Equation(this, Expression.constant(rhs))