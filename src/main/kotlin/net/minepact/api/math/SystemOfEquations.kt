package net.minepact.api.math

class SystemOfEquations(val equations: List<Equation>) {
    constructor(vararg equations: Equation) : this(equations.toList())
    val symbols: List<Symbol> = equations
        .flatMap { it.getSymbols() }
        .distinct()
        .sortedBy { it.name }

    fun solve(): Map<Symbol, Double>? {
        val n = symbols.size
        val m = equations.size
        if (m < n) return null

        val augmented = Matrix(m, n + 1) { row, col ->
            val std = equations[row].standardForm
            if (col < n) {
                val sym = symbols[col]
                std.terms
                    .firstOrNull { it.variables == mapOf(sym to 1) }
                    ?.coefficient?.toDouble() ?: 0.0
            } else {
                -std.constantTerm().toDouble()
            }
        }

        val rref = augmented.rref()
        val result = mutableMapOf<Symbol, Double>()
        for (row in 0 until m) {
            val pivotCol = (0 until n).firstOrNull { kotlin.math.abs(rref[row, it]) > 1e-9 }
                ?: continue
            if (pivotCol >= n) return null
            result[symbols[pivotCol]] = rref[row, n]
        }
        return if (result.size == n) result else null
    }

    override fun toString(): String = equations.joinToString("\n") { "  $it" }.let { "System:\n$it" }
}