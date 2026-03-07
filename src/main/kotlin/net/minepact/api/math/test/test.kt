package net.minepact.api.math.test

import net.minepact.api.math.*
import net.minepact.api.math.helper.*
import net.minepact.api.math.helper.matrix.*
import net.minepact.api.math.helper.symbol.*
import net.minepact.api.math.helper.term.*
import net.minepact.api.math.helper.vector.*

fun main() {
    header("1. RATIONAL ARITHMETIC")
    val a = Rational(3, 4)
    val b = Rational(5, 6)
    println("  a         = $a")
    println("  b         = $b")
    println("  a + b     = ${a + b}")
    println("  a - b     = ${a - b}")
    println("  a × b     = ${a * b}")
    println("  a / b     = ${a / b}")
    println("  a²        = ${a.pow(2)}")
    println("  a + 1     = ${a + 1}")
    println("  LaTeX(a)  = ${a.toLaTeX()}")

    header("2. SYMBOLS & TERMS")
    val x = sym("x")
    val y = sym("y")

    val t1 = 3 * x.pow(2)     // 3x²
    val t2 = -2 * x            // -2x
    val t3 = term(7)           // 7

    println("  t1        = $t1")
    println("  t2        = $t2")
    println("  t1 × t2   = ${t1 * t2}")
    println("  t1 eval x=2 → ${t1.evaluate(mapOf(x to 2.0))}")
    println("  ∂t1/∂x    = ${t1.derivative(x)}")
    println("  ∫t1 dx    = ${t1.integral(x)}")

    header("3. EXPRESSIONS")
    val f = (3 * x.pow(2)) + (-2 * x) + 1    // 3x² - 2x + 1
    val g = x + 1                              // x + 1

    println("  f         = $f")
    println("  g         = $g")
    println("  f + g     = ${f + g}")
    println("  f × g     = ${f * g}")
    println("  f(2)      = ${f.evaluate(x to 2.0)}")
    println("  ∂f/∂x     = ${f.derivative(x)}")
    println("  ∫f dx     = ${f.integral(x)}")
    println("  ∫₀¹ f dx  = ${"%.6f".format(f.definiteIntegral(x, 0.0, 1.0))}")

    val multivar = (2 * x.pow(2)) + (3 * x * y) - (y.pow(2))
    println("  2x²+3xy-y²(x=1,y=2) = ${multivar.evaluate(x to 1.0, y to 2.0)}")

    header("4. POLYNOMIAL")
    //  p = x³ - 6x² + 11x - 6   (roots: 1, 2, 3)
    val p = Polynomial.of(x, -6, 11, -6, 1)  // ascending: [-6, 11, -6, 1]
    println("  p         = $p")
    println("  degree    = ${p.degree}")
    println("  coeffs ↓  = ${p.coefficients}")
    println("  roots     = ${p.roots().map { "%.4f".format(it) }}")
    println("  p(2)      = ${p.evaluate(2.0)}")
    println("  p'        = ${p.derivative()}")
    println("  ∫p dx     = ${p.integral()}")
    println("  ∫₀³ p dx  = ${"%.6f".format(p.definiteIntegral(0.0, 3.0))}")

    val q = Polynomial.of(x, -2, 1)  // x - 2
    val (quot, rem) = p.divideBy(q)
    println("  p ÷ (x-2) = $quot  remainder $rem")

    header("5. EQUATIONS")
    // Quadratic: x² - 5x + 6 = 0
    val eq1 = (x.pow(2) - 5*x + 6) eq 0
    println("  $eq1")
    println("  roots     = ${eq1.solveFor(x).map { "%.4f".format(it) }}")

    // Linear: 2x + 3 = 11
    val eq2 = (2*x + 3) eq 11
    println("  $eq2")
    println("  x = ${eq2.solveFor(x).map { "%.4f".format(it) }}")

    // Satisfaction check
    println("  x=2 satisfies x²-5x+6=0? ${eq1.isSatisfiedBy(x to 2.0)}")
    println("  x=4 satisfies x²-5x+6=0? ${eq1.isSatisfiedBy(x to 4.0)}")

    // Multi-variable solve
    val m = sym("m"); val c = sym("c")
    val lineEq = y eq (m * x + c)  // y = mx + c
    val xSol = lineEq.solveFor(x, mapOf(y to 10.0, m to 2.0, c to 2.0))
    println("  y=mx+c solve for x given y=10,m=2,c=2: x=${xSol.map{"%.4f".format(it)}}")

    header("6. SYSTEM OF EQUATIONS")
    //  2x + 3y = 12
    //  4x - 1y =  2
    val (sx, sy) = listOf(sym("x"), sym("y"))
    val system = SystemOfEquations(
        (2 * sx + 3 * sy) eq 12,
        (4 * sx -     sy) eq  2
    )
    println("  $system")
    val sol = system.solve()
    println("  Solution: ${sol?.map { (s, v) -> "$s = ${"%.4f".format(v)}" }}")

    header("7. MATRIX")
    val A = Matrix.of(
        row(1, 2, 3),
        row(4, 5, 6),
        row(7, 8, 9)
    )
    val B = Matrix.of(
        row(9, 8, 7),
        row(6, 5, 4),
        row(3, 2, 1)
    )
    println("  A =\n$A")
    println("  B =\n$B")
    println("  A + B =\n${A + B}")
    println("  A × B =\n${A * B}")
    println("  Aᵀ =\n${A.transpose()}")
    println("  tr(A) = ${A.trace}")
    println("  rank(A) = ${A.rank()}")

    val C = Matrix.of(row(2, 1), row(5, 3))
    println("  C =\n$C")
    println("  det(C) = ${C.determinant()}")
    println("  C⁻¹ =\n${C.inverse()}")
    println("  C × C⁻¹ =\n${C * C.inverse()}")

    val (L, U) = C.luDecompose()
    println("  LU: L=\n$L\n  U=\n$U")

    header("8. VECTOR")
    val u = vec(1.0, 2.0, 3.0)
    val v = vec(4.0, 5.0, 6.0)
    println("  u           = $u")
    println("  v           = $v")
    println("  u + v       = ${u + v}")
    println("  u · v       = ${u dot v}")
    println("  u × v       = ${u cross v}")
    println("  |u|         = ${"%.6f".format(u.magnitude)}")
    println("  û           = ${u.normalized()}")
    println("  angle(u,v)  = ${"%.6f".format(u.angle(v))} rad")
    println("  proj_v(u)   = ${u.projectOnto(v)}")
    println("  A × u       = ${C * vec(1.0, 2.0)}")

    header("9. LATEX OUTPUT")
    println("  f     → ${f.toLaTeX()}")
    println("  p     → ${p.toLaTeX()}")
    println("  eq1   → ${eq1.toLaTeX()}")
    println("  C     → ${C.toLaTeX()}")
    println("  u     → ${u.toLaTeX()}")
    println("  3/4   → ${Rational(3,4).toLaTeX()}")
}

private fun header(title: String) = println("\n---- $title ----")