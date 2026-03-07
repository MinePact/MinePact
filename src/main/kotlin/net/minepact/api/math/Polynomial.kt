package net.minepact.api.math

import kotlin.math.abs
import kotlin.math.sqrt

class Polynomial(
    val variable: Symbol,
    expression: Expression
) {
    val expression: Expression

    init {
        val extra = expression.getSymbols() - variable
        require(extra.isEmpty()) { "Polynomial must be single-variable; unexpected symbols: $extra" }
        this.expression = expression
    }

    companion object {
        fun of(variable: Symbol, vararg coefficients: Rational): Polynomial {
            val terms = coefficients.mapIndexed { deg, coeff ->
                when {
                    coeff.isZero() -> null
                    deg == 0       -> Term(coeff)
                    else           -> Term(coeff, mapOf(variable to deg))
                }
            }.filterNotNull()
            return Polynomial(variable, Expression(terms))
        }

        fun of(variable: Symbol, vararg coefficients: Int): Polynomial =
            of(variable, *coefficients.map { Rational(it) }.toTypedArray())
        fun wrap(variable: Symbol, expression: Expression) = Polynomial(variable, expression)
    }

    val degree: Int
        get() = expression.terms.maxOfOrNull { it.degreeOf(variable) } ?: 0
    val leadingCoefficient: Rational
        get() = expression.terms.maxByOrNull { it.degreeOf(variable) }?.coefficient ?: Rational.ZERO
    val coefficients: List<Rational>
        get() = (degree downTo 0).map { d ->
            expression.terms
                .firstOrNull { it.degreeOf(variable) == d }?.coefficient
                ?: Rational.ZERO
        }
    val coefficientsAscending: List<Rational>
        get() = coefficients.reversed()

    val isZero: Boolean get() = expression.isZero
    val isConstant: Boolean get() = degree == 0 && !isZero

    fun evaluate(x: Double): Double =
        coefficients.fold(0.0) { acc, c -> acc * x + c.toDouble() }
    fun evaluate(x: Rational): Rational =
        coefficients.fold(Rational.ZERO) { acc, c -> acc * x + c }

    operator fun plus(other: Polynomial): Polynomial = Polynomial(variable, expression + other.expression)
    operator fun minus(other: Polynomial): Polynomial = Polynomial(variable, expression - other.expression)
    operator fun times(other: Polynomial): Polynomial = Polynomial(variable, expression * other.expression)
    operator fun times(scalar: Rational): Polynomial = Polynomial(variable, expression * scalar)
    operator fun times(scalar: Int): Polynomial = times(Rational(scalar))
    operator fun unaryMinus(): Polynomial = Polynomial(variable, -expression)

    fun pow(exp: Int): Polynomial = Polynomial(variable, expression.pow(exp))

    fun derivative(): Polynomial = Polynomial(variable, expression.derivative(variable))
    fun integral(): Polynomial = Polynomial(variable, expression.integral(variable))
    fun definiteIntegral(a: Double, b: Double): Double =
        expression.definiteIntegral(variable, a, b)

    fun roots(): List<Double> = when (degree) {
        0    -> emptyList()
        1    -> listOf(solveLinear())
        2    -> solveQuadratic()
        else -> solveNumerical()
    }

    private fun solveLinear(): Double {
        val a = coefficientsAscending.getOrElse(1) { Rational.ZERO }
        val b = coefficientsAscending.getOrElse(0) { Rational.ZERO }
        return (-b / a).toDouble()
    }
    private fun solveQuadratic(): List<Double> {
        val a = coefficients.getOrElse(0) { Rational.ZERO }.toDouble()
        val b = coefficients.getOrElse(1) { Rational.ZERO }.toDouble()
        val c = coefficients.getOrElse(2) { Rational.ZERO }.toDouble()
        val disc = b * b - 4 * a * c
        return when {
            disc < 0 -> emptyList()
            disc == 0.0 -> listOf(-b / (2 * a))
            else -> listOf((-b + sqrt(disc)) / (2 * a), (-b - sqrt(disc)) / (2 * a))
        }
    }
    private fun solveNumerical(
        range: Double   = 1_000.0,
        steps: Int      = 2_000,
        tol: Double   = 1e-10,
        maxIt: Int      = 150
    ): List<Double> {
        val roots = mutableListOf<Double>()
        val step  = 2.0 * range / steps
        var prev  = evaluate(-range)

        var x = -range + step
        while (x <= range) {
            val curr = evaluate(x)
            if (prev * curr <= 0.0) {
                newtonRaphson(x - step / 2.0, tol, maxIt)
                    ?.takeIf { r -> roots.none { abs(it - r) < tol } }
                    ?.let { roots.add(it) }
            }
            prev = curr
            x += step
        }
        return roots.sorted()
    }

    private fun newtonRaphson(x0: Double, tol: Double, maxIter: Int): Double? {
        val d = derivative()
        var x = x0
        repeat(maxIter) {
            val fx  = evaluate(x)
            val fpx = d.evaluate(x)
            if (abs(fpx) < 1e-15) return null
            val xn = x - fx / fpx
            if (abs(xn - x) < tol) return xn
            x = xn
        }
        return if (abs(evaluate(x)) < tol) x else null
    } // root finding algo

    fun divideBy(divisor: Polynomial): Pair<Polynomial, Polynomial> {
        require(variable == divisor.variable) { "Variables must match for polynomial division" }
        require(!divisor.isZero) { "Cannot divide by the zero polynomial" }

        var remainder = this
        val quotientTerms = mutableListOf<Term>()

        while (!remainder.isZero && remainder.degree >= divisor.degree) {
            val lRem = remainder.expression.terms.maxByOrNull { it.degreeOf(variable) }!!
            val lDiv = divisor.expression.terms.maxByOrNull { it.degreeOf(variable) }!!
            val qt   = lRem / lDiv
            quotientTerms.add(qt)
            remainder -= Polynomial(variable, qt.toExpression() * divisor.expression)
        }

        return Polynomial(variable, Expression(quotientTerms)) to remainder
    }

    override fun toString() = expression.toString()
    fun toLaTeX()           = expression.toLaTeX()
}