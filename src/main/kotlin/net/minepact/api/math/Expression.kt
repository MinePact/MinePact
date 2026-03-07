package net.minepact.api.math

class Expression private constructor(
    val terms: List<Term>
) {

    companion object {
        val ZERO = Expression(emptyList())
        val ONE = Expression(listOf(Term(Rational.ONE)))

        operator fun invoke(terms: List<Term>): Expression = Expression(simplify(terms))

        fun constant(value: Rational) = Expression(listOf(Term(value)))
        fun constant(value: Int) = constant(Rational(value))
        fun of(vararg terms: Term) = Expression(terms.toList())

        private fun simplify(raw: List<Term>): List<Term> {
            val groups = LinkedHashMap<Map<Symbol, Int>, Rational>()
            for (term in raw) {
                groups.merge(term.variables, term.coefficient, Rational::plus)
            }
            return groups
                .filterValues { !it.isZero() }
                .map { (vars, coeff) -> Term(coeff, vars) }
                .sortedWith(compareByDescending<Term> { it.degree }
                .thenBy { it.variables.keys.minOfOrNull { s -> s.name } ?: "" })
        }
    }

    val isZero: Boolean get() = terms.isEmpty()
    val isConstant: Boolean get() = terms.all { it.isConstant }

    fun getSymbols(): Set<Symbol> = terms.flatMap { it.getSymbols() }.toSet()
    fun constantTerm(): Rational = terms.firstOrNull { it.isConstant }?.coefficient ?: Rational.ZERO
    fun leadingTerm(): Term? = terms.firstOrNull()

    val size: Int get() = terms.size

    operator fun unaryMinus(): Expression = Expression(terms.map { -it })
    operator fun unaryPlus(): Expression = this

    operator fun plus(other: Expression): Expression = Expression(terms + other.terms)
    operator fun plus(other: Term): Expression = Expression(terms + other)
    operator fun plus(other: Symbol): Expression = this + other.toTerm()
    operator fun plus(n: Int): Expression = this + constant(n)
    operator fun plus(n: Rational): Expression = this + constant(n)

    operator fun minus(other: Expression): Expression = this + (-other)
    operator fun minus(other: Term): Expression = this + (-other)
    operator fun minus(other: Symbol): Expression = this - other.toTerm()
    operator fun minus(n: Int): Expression = this - constant(n)
    operator fun minus(n: Rational): Expression = this - constant(n)

    operator fun times(scalar: Rational): Expression = Expression(terms.map { it * scalar })
    operator fun times(scalar: Int): Expression = times(Rational(scalar))
    operator fun times(other: Term): Expression = Expression(terms.map { it * other })
    operator fun times(other: Symbol): Expression = this * other.toTerm()
    operator fun times(other: Expression): Expression =
        Expression(terms.flatMap { a -> other.terms.map { b -> a * b } })

    fun pow(exp: Int): Expression {
        require(exp >= 0) { "Expression power must be non-negative" }
        return when (exp) {
            0    -> ONE
            1    -> this
            else -> (1 until exp).fold(this) { acc, _ -> acc * this }
        }
    }

    fun evaluate(assignments: Map<Symbol, Double>): Double =
        terms.sumOf { it.evaluate(assignments) }
    fun evaluate(vararg pairs: Pair<Symbol, Double>): Double = evaluate(pairs.toMap())
    fun evaluateRational(assignments: Map<Symbol, Rational>): Rational =
        terms.fold(Rational.ZERO) { acc, t -> acc + t.evaluateRational(assignments) }
    fun evaluateRational(vararg pairs: Pair<Symbol, Rational>): Rational =
        evaluateRational(pairs.toMap())

    fun substitute(symbol: Symbol, value: Rational): Expression =
        Expression(terms.map { it.substitute(symbol, value) })
    fun substitute(symbol: Symbol, replacement: Expression): Expression {
        val parts = terms.map { term ->
            val exp = term.variables[symbol] ?: return@map term.toExpression()
            val baseVars = term.variables.toMutableMap().apply { remove(symbol) }
            val baseTerm = Term(term.coefficient, baseVars)
            baseTerm.toExpression() * replacement.pow(exp)
        }
        return parts.reduceOrNull { a, b -> a + b } ?: ZERO
    }
    fun substitute(symbol: Symbol, value: Double): Expression =
        substitute(symbol, Rational.approx(value))

    fun derivative(symbol: Symbol): Expression =
        Expression(terms.map { it.derivative(symbol) })
    fun integral(symbol: Symbol): Expression =
        Expression(terms.map { it.integral(symbol) })
    fun definiteIntegral(symbol: Symbol, a: Double, b: Double): Double {
        val anti = integral(symbol)
        return anti.evaluate(mapOf(symbol to b)) - anti.evaluate(mapOf(symbol to a))
    }

    infix fun eq(rhs: Expression): Equation = Equation(this, rhs)
    infix fun eq(rhs: Int): Equation = Equation(this, constant(rhs))
    infix fun eq(rhs: Rational): Equation = Equation(this, constant(rhs))

    override fun toString(): String =
        if (terms.isEmpty()) "0"
        else terms.joinToString(" + ") { it.toString() }.replace("+ -", "- ")
    fun toLaTeX(): String =
        if (terms.isEmpty()) "0"
        else terms.joinToString(" + ") { it.toLaTeX() }.replace("+ -", "- ")
    
    override fun equals(other: Any?): Boolean =
        other is Expression && terms.toSet() == other.terms.toSet()
    override fun hashCode(): Int = terms.toSet().hashCode()
}