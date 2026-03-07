package net.minepact.api.math

import kotlin.math.abs
import kotlin.math.pow

class Equation(
    val lhs: Expression,
    val rhs: Expression = Expression.ZERO
) {
    val standardForm: Expression get() = lhs - rhs

    fun getSymbols(): Set<Symbol> = lhs.getSymbols() + rhs.getSymbols()

    fun isSatisfiedBy(assignments: Map<Symbol, Double>, tolerance: Double = 1e-9): Boolean =
        abs(lhs.evaluate(assignments) - rhs.evaluate(assignments)) < tolerance
    fun isSatisfiedBy(vararg pairs: Pair<Symbol, Double>): Boolean =
        isSatisfiedBy(pairs.toMap())

    fun solveFor(symbol: Symbol): List<Double> {
        val std = standardForm
        val extra = std.getSymbols() - symbol
        require(extra.isEmpty()) {
                "Cannot solve for $symbol while other unresolved symbols are present: $extra.\n" +
                "Use solveFor(symbol, assignments) to supply values for those symbols."
        }
        return Polynomial.wrap(symbol, std).roots()
    }
    fun solveFor(symbol: Symbol, assignments: Map<Symbol, Double>): List<Double> {
        val std = standardForm
        val degreeCoefficients = mutableMapOf<Int, Double>()

        for (term in std.terms) {
            val symExp = term.degreeOf(symbol)
            val otherCoeff = term.variables
                .filter { (s, _) -> s != symbol }
                .entries
                .fold(term.coefficient.toDouble()) { acc, (s, exp) ->
                    val v = assignments[s]
                        ?: throw IllegalArgumentException("No value supplied for symbol '$s'")
                    acc * v.pow(exp.toDouble())
                }
            degreeCoefficients.merge(symExp, otherCoeff, Double::plus)
        }

        val maxDeg = degreeCoefficients.keys.maxOrNull() ?: 0
        val coeffList = (0..maxDeg).map { d ->
            Rational.approx(degreeCoefficients[d] ?: 0.0)
        }
        return Polynomial.of(symbol, *coeffList.toTypedArray()).roots()
    }
    fun solveFor(symbol: Symbol, vararg pairs: Pair<Symbol, Double>): List<Double> =
        solveFor(symbol, pairs.toMap())
    
    fun addToBothSides(expr: Expression): Equation = Equation(lhs + expr, rhs + expr)
    fun multiplyBothSides(scalar: Rational): Equation = Equation(lhs * scalar, rhs * scalar)
    fun multiplyBothSides(scalar: Int): Equation = multiplyBothSides(Rational(scalar))

    fun substitute(symbol: Symbol, value: Rational): Equation =
        Equation(lhs.substitute(symbol, value), rhs.substitute(symbol, value))
    fun substitute(symbol: Symbol, expr: Expression): Equation =
        Equation(lhs.substitute(symbol, expr), rhs.substitute(symbol, expr))

    override fun toString() = "$lhs = $rhs"
    fun toLaTeX() = "${lhs.toLaTeX()} = ${rhs.toLaTeX()}"
}