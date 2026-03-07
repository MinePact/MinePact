package net.minepact.api.math.helper.equation

import net.minepact.api.math.Equation
import net.minepact.api.math.Expression
import net.minepact.api.math.Rational
import net.minepact.api.math.Term

@Suppress("EXTENSION_SHADOWED_BY_MEMBER") infix fun Term.eq(rhs: Expression): Equation = Equation(this.toExpression(), rhs)
@Suppress("EXTENSION_SHADOWED_BY_MEMBER") infix fun Term.eq(rhs: Int): Equation = Equation(this.toExpression(), Expression.constant(rhs))
@Suppress("EXTENSION_SHADOWED_BY_MEMBER") infix fun Term.eq(rhs: Rational): Equation = Equation(this.toExpression(), Expression.constant(rhs))