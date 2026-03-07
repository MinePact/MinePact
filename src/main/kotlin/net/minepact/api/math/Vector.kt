package net.minepact.api.math

import kotlin.math.acos
import kotlin.math.abs
import kotlin.math.sqrt

class Vector(val components: DoubleArray) {
    val size: Int get() = components.size

    companion object {
        fun of(vararg components: Double): Vector = Vector(components.copyOf())
        fun of(vararg components: Int): Vector = Vector(components.map(Int::toDouble).toDoubleArray())
        fun zero(n: Int): Vector = Vector(DoubleArray(n))
        fun basis(n: Int, i: Int): Vector {
            require(i in 0 until n) { "Index $i out of range for dimension $n" }
            return Vector(DoubleArray(n) { if (it == i) 1.0 else 0.0 })
        }
    }

    operator fun get(i: Int): Double = components[i]

    val x: Double get() = components[0]
    val y: Double get() = if (size >= 2) components[1] else throw IndexOutOfBoundsException("No y component")
    val z: Double get() = if (size >= 3) components[2] else throw IndexOutOfBoundsException("No z component")

    operator fun plus(other: Vector): Vector {
        requireSameSize(other, "addition")
        return Vector(DoubleArray(size) { i -> components[i] + other.components[i] })
    }
    operator fun minus(other: Vector): Vector {
        requireSameSize(other, "subtraction")
        return Vector(DoubleArray(size) { i -> components[i] - other.components[i] })
    }

    operator fun times(scalar: Double): Vector = Vector(DoubleArray(size) { components[it] * scalar })
    operator fun times(scalar: Int): Vector    = times(scalar.toDouble())
    operator fun div(scalar: Double): Vector   = times(1.0 / scalar)
    operator fun unaryMinus(): Vector          = times(-1.0)
    operator fun unaryPlus(): Vector          = this

    infix fun dot(other: Vector): Double {
        requireSameSize(other, "dot product")
        return components.indices.sumOf { components[it] * other.components[it] }
    }
    infix fun cross(other: Vector): Vector {
        require(size == 3 && other.size == 3) { "Cross product is only defined for 3-D vectors" }
        return Vector(
            doubleArrayOf(
                components[1] * other.components[2] - components[2] * other.components[1],
                components[2] * other.components[0] - components[0] * other.components[2],
                components[0] * other.components[1] - components[1] * other.components[0]
            )
        )
    }

    infix fun outer(other: Vector): Matrix =
        Matrix(size, other.size) { r, c -> components[r] * other.components[c] }

    val magnitude: Double get() = sqrt(this dot this)
    val magnitudeSquared: Double get() = this dot this
    val l1Norm: Double get() = components.sumOf { abs(it) }
    val lInfNorm: Double get() = components.maxOf { abs(it) }

    fun distanceTo(other: Vector): Double = (this - other).magnitude

    fun normalized(): Vector {
        val mag = magnitude
        require(mag > 1e-15) { "Cannot normalise the zero vector" }
        return this / mag
    }

    fun angle(other: Vector): Double =
        acos(((this dot other) / (magnitude * other.magnitude)).coerceIn(-1.0, 1.0))
    fun angleDeg(other: Vector): Double = Math.toDegrees(angle(other))

    fun isParallelTo(other: Vector, tol: Double = 1e-9): Boolean =
        if (size == 3) (this cross other).magnitude < tol
        else {
            val a = this.normalized()
            val b = other.normalized()
            (a - b).magnitude < tol || (a + b).magnitude < tol
        }
    fun isOrthogonalTo(other: Vector, tol: Double = 1e-9): Boolean =
        abs(this dot other) < tol

    fun projectOnto(onto: Vector): Vector = onto * ((this dot onto) / onto.magnitudeSquared)
    fun rejectFrom(onto: Vector): Vector = this - projectOnto(onto)
    fun scalarProjection(onto: Vector): Double = (this dot onto) / onto.magnitude

    fun reflect(normal: Vector): Vector {
        val n = normal.normalized()
        return this - n * (2.0 * (this dot n))
    }

    fun toColumnMatrix(): Matrix = Matrix(size, 1) { r, _ -> components[r] }
    fun toRowMatrix(): Matrix = Matrix(1, size) { _, c -> components[c] }
    fun toList(): List<Double> = components.toList()

    override fun toString(): String =
        "(${components.joinToString(", ") { "%.4g".format(it) }})"
    fun toLaTeX(): String {
        val body = components.joinToString(" \\\\\n") { "%.4g".format(it) }
        return "\\begin{pmatrix}\n$body\n\\end{pmatrix}"
    }

    override fun equals(other: Any?): Boolean =
        other is Vector && size == other.size &&
                components.indices.all { abs(components[it] - other.components[it]) < 1e-9 }
    override fun hashCode(): Int = components.contentHashCode()

    private fun requireSameSize(other: Vector, op: String) {
        require(size == other.size) { "Vector sizes must match for $op: $size vs ${other.size}" }
    }
}