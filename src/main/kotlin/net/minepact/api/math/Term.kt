package net.minepact.api.math

import kotlin.math.pow as dpow

class Term(
    val coefficient: Rational,
    variables: Map<Symbol, Int> = emptyMap()
) {
    val variables: Map<Symbol, Int> = variables.filter { (_, exp) -> exp != 0 }

    val isConstant: Boolean get() = variables.isEmpty()
    val isZero: Boolean get() = coefficient.isZero()

    val degree: Int get() = variables.values.sumOf { it }
    fun degreeOf(symbol: Symbol): Int = variables[symbol] ?: 0
    fun getSymbols(): Set<Symbol> = variables.keys.toSet()
    fun isLikeTerm(other: Term): Boolean = variables == other.variables

    operator fun unaryMinus(): Term = Term(-coefficient, variables)
    operator fun unaryPlus(): Term = this

    operator fun times(scalar: Rational): Term = Term(coefficient * scalar, variables)
    operator fun times(scalar: Int): Term = times(Rational(scalar))
    operator fun times(scalar: Long): Term = times(Rational(scalar))
    operator fun div(scalar: Rational): Term = Term(coefficient / scalar, variables)
    operator fun div(scalar: Int): Term = div(Rational(scalar))

    operator fun times(other: Term): Term {
        val newVars = (variables.keys + other.variables.keys).associateWith { sym ->
            (variables[sym] ?: 0) + (other.variables[sym] ?: 0)
        }
        return Term(coefficient * other.coefficient, newVars)
    }
    operator fun div(other: Term): Term {
        require(!other.coefficient.isZero()) { "Division by zero-coefficient term" }
        val newVars = (variables.keys + other.variables.keys).associateWith { sym ->
            (variables[sym] ?: 0) - (other.variables[sym] ?: 0)
        }
        return Term(coefficient / other.coefficient, newVars)
    }
    fun pow(exp: Int): Term {
        require(exp >= 0) { "Term exponent must be non-negative" }
        return Term(
            coefficient.pow(exp),
            variables.mapValues { (_, e) -> e * exp }
        )
    }

    fun toExpression(): Expression = Expression(listOf(this))

    operator fun times(symbol: Symbol): Term = this * symbol.toTerm()

    operator fun plus(other: Term): Expression = Expression(listOf(this, other))
    operator fun plus(other: Expression): Expression = this.toExpression() + other
    operator fun plus(other: Symbol): Expression = this + other.toTerm()
    operator fun plus(n: Int): Expression = this + Term(Rational(n))
    operator fun plus(n: Rational): Expression = this + Term(n)

    operator fun minus(other: Term): Expression = Expression(listOf(this, -other))
    operator fun minus(other: Expression): Expression = this.toExpression() - other
    operator fun minus(other: Symbol): Expression = this - other.toTerm()
    operator fun minus(n: Int): Expression = this - Term(Rational(n))
    operator fun minus(n: Rational): Expression = this - Term(n)

    fun evaluate(assignments: Map<Symbol, Double>): Double {
        var result = coefficient.toDouble()
        for ((sym, exp) in variables) {
            val value = assignments[sym]
                ?: throw IllegalArgumentException("No value provided for symbol '$sym'")
            result *= value.dpow(exp.toDouble())
        }
        return result
    }
    fun evaluateRational(assignments: Map<Symbol, Rational>): Rational {
        var result = coefficient
        for ((sym, exp) in variables) {
            val value = assignments[sym]
                ?: throw IllegalArgumentException("No value provided for symbol '$sym'")
            result *= if (exp >= 0) value.pow(exp) else value.reciprocal().pow(-exp)
        }
        return result
    }

    fun derivative(symbol: Symbol): Term {
        val exp = variables[symbol] ?: return Term(Rational.ZERO)
        val newCoeff = coefficient * Rational(exp)
        val newVars = variables.toMutableMap().apply {
            if (exp - 1 == 0) remove(symbol) else this[symbol] = exp - 1
        }
        return Term(newCoeff, newVars)
    }
    fun integral(symbol: Symbol): Term {
        val exp = variables[symbol] ?: 0
        val newExp = exp + 1
        val newVars = variables.toMutableMap().apply { this[symbol] = newExp }
        return Term(coefficient / Rational(newExp), newVars)
    }

    fun substitute(symbol: Symbol, value: Rational): Term {
        val exp = variables[symbol] ?: return this
        val newCoeff = coefficient * value.pow(exp)
        val newVars = variables.toMutableMap().apply { remove(symbol) }
        return Term(newCoeff, newVars)
    }

    override fun toString(): String = format(
        coefficientFormat = { it.toString() },
        varFmt = { sym, exp -> if (exp == 1) sym.name else "${sym.name}^$exp" }
    )
    fun toLaTeX(): String = format(
        coefficientFormat = { it.toLaTeX() },
        varFmt = { sym, exp -> if (exp == 1) sym.name else "${sym.name}^{$exp}" }
    )
    private fun format(coefficientFormat: (Rational) -> String, varFmt: (Symbol, Int) -> String): String {
        if (coefficient.isZero()) return "0"
        val varPart = variables.entries
            .sortedBy { it.key.name }
            .joinToString("") { (sym, exp) -> varFmt(sym, exp) }
        return when {
            varPart.isEmpty() -> coefficientFormat(coefficient)
            coefficient == Rational.ONE -> varPart
            coefficient == -Rational.ONE -> "-$varPart"
            else -> "${coefficientFormat(coefficient)}$varPart"
        }
    }

    override fun equals(other: Any?): Boolean =
        other is Term && coefficient == other.coefficient && variables == other.variables
    override fun hashCode(): Int = 31 * coefficient.hashCode() + variables.hashCode()
}