package net.minepact.api.math.helper

import net.minepact.api.math.*

fun expr(vararg terms: Term): Expression = Expression(terms.toList())
fun expr(value: Int): Expression = Expression.constant(value)
fun expr(value: Rational): Expression = Expression.constant(value)

fun term(value: Int): Term = Term(Rational(value))
fun term(value: Rational): Term = Term(value)
fun poly(variable: Symbol, vararg coefficients: Int): Polynomial = Polynomial.of(variable, *coefficients)
fun poly(variable: Symbol, vararg coefficients: Rational): Polynomial = Polynomial.of(variable, *coefficients)
fun mat(vararg rows: DoubleArray): Matrix = Matrix.of(*rows)

fun Int.toExpression(): Expression = Expression.constant(this)
fun Rational.toExpression(): Expression = Expression.constant(this)

fun Int.toTerm(): Term = Term(Rational(this))
fun Rational.toTerm(): Term = Term(this)

fun eq(lhs: Expression, rhs: Expression): Equation = Equation(lhs, rhs)
fun eq(lhs: Expression, rhs: Int): Equation = Equation(lhs, Expression.constant(rhs))
fun eq(lhs: Term, rhs: Term): Equation = Equation(lhs.toExpression(), rhs.toExpression())