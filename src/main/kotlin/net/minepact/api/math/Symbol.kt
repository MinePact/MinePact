package net.minepact.api.math

data class Symbol(val name: String) {
    fun toTerm(): Term = Term(Rational.ONE, mapOf(this to 1))
    fun toExpression(): Expression = toTerm().toExpression()

    fun pow(exponent: Int): Term = Term(Rational.ONE, mapOf(this to exponent))

    operator fun times(coefficient: Rational): Term = Term(coefficient, mapOf(this to 1))
    operator fun times(coefficient: Int): Term = this * Rational(coefficient)
    operator fun times(other: Symbol): Term =
        if (this == other) Term(Rational.ONE, mapOf(this to 2))
        else Term(Rational.ONE, mapOf(this to 1, other to 1))
    operator fun times(other: Term): Term = toTerm() * other

    operator fun plus(other: Symbol): Expression = toExpression() + other.toExpression()
    operator fun plus(other: Term): Expression = toExpression() + other
    operator fun plus(other: Expression): Expression = toExpression() + other
    operator fun plus(n: Int): Expression = toExpression() + n
    operator fun plus(n: Rational): Expression = toExpression() + n

    operator fun minus(other: Symbol): Expression = toExpression() - other.toExpression()
    operator fun minus(other: Term): Expression = toExpression() - other
    operator fun minus(other: Expression): Expression = toExpression() - other
    operator fun minus(n: Int): Expression = toExpression() - n
    operator fun minus(n: Rational): Expression = toExpression() - n

    operator fun unaryMinus(): Term = Term(Rational.NEGATIVE_ONE, mapOf(this to 1))

    infix fun eq(rhs: Expression): Equation = Equation(toExpression(), rhs)
    infix fun eq(rhs: Int): Equation = Equation(toExpression(), Expression.constant(rhs))
    infix fun eq(rhs: Rational): Equation = Equation(toExpression(), Expression.constant(rhs))

    override fun toString() = name
}