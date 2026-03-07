package net.minepact.api.math

import kotlin.math.abs
import kotlin.math.sqrt

class Matrix private constructor(private val data: Array<DoubleArray>) {

    val rows: Int = data.size
    val cols: Int = data.firstOrNull()?.size ?: 0

    companion object {
        operator fun invoke(rows: Int, cols: Int, init: (Int, Int) -> Double = { _, _ -> 0.0 }): Matrix =
            Matrix(Array(rows) { r -> DoubleArray(cols) { c -> init(r, c) } })
        fun of(vararg rows: DoubleArray): Matrix =
            Matrix(Array(rows.size) { rows[it].copyOf() })

        fun fromList(data: List<List<Double>>): Matrix {
            require(data.isNotEmpty() && data.all { it.size == data[0].size }) {
                "All rows must have the same length"
            }
            return Matrix(Array(data.size) { r -> data[r].toDoubleArray() })
        }

        fun identity(n: Int): Matrix = Matrix(n, n) { r, c -> if (r == c) 1.0 else 0.0 }
        fun zeros(rows: Int, cols: Int): Matrix = Matrix(rows, cols)
        fun ones(rows: Int, cols: Int): Matrix = Matrix(rows, cols) { _, _ -> 1.0 }
        fun diagonal(vararg values: Double): Matrix = Matrix(values.size, values.size) { r, c ->
            if (r == c) values[r] else 0.0
        }
    }

    operator fun get(row: Int, col: Int): Double = data[row][col]

    fun row(r: Int): DoubleArray = data[r].copyOf()
    fun col(c: Int): DoubleArray = DoubleArray(rows) { r -> data[r][c] }

    fun rowVector(r: Int): Vector = Vector(row(r))
    fun colVector(c: Int): Vector = Vector(col(c))

    fun subMatrix(rowRange: IntRange, colRange: IntRange): Matrix =
        Matrix(rowRange.count(), colRange.count()) { r, c -> data[rowRange.first + r][colRange.first + c] }

    operator fun plus(other: Matrix): Matrix {
        requireSameDimensions(other, "addition")
        return Matrix(rows, cols) { r, c -> data[r][c] + other.data[r][c] }
    }
    operator fun minus(other: Matrix): Matrix {
        requireSameDimensions(other, "subtraction")
        return Matrix(rows, cols) { r, c -> data[r][c] - other.data[r][c] }
    }
    operator fun times(other: Matrix): Matrix {
        require(cols == other.rows) {
            "Incompatible dimensions for multiplication: ${rows}x${cols} × ${other.rows}x${other.cols}"
        }
        return Matrix(rows, other.cols) { r, c ->
            (0 until cols).sumOf { k -> data[r][k] * other.data[k][c] }
        }
    }
    operator fun times(v: Vector): Vector {
        require(cols == v.size) { "Matrix cols ($cols) must match vector size (${v.size})" }
        return Vector(DoubleArray(rows) { r -> (0 until cols).sumOf { c -> data[r][c] * v[c] } })
    }

    operator fun times(scalar: Double): Matrix = Matrix(rows, cols) { r, c -> data[r][c] * scalar }
    operator fun times(scalar: Int): Matrix = times(scalar.toDouble())
    operator fun div(scalar: Double): Matrix = times(1.0 / scalar)
    operator fun unaryMinus(): Matrix = times(-1.0)

    fun hadamard(other: Matrix): Matrix {
        requireSameDimensions(other, "Hadamard product")
        return Matrix(rows, cols) { r, c -> data[r][c] * other.data[r][c] }
    }

    fun transpose(): Matrix = Matrix(cols, rows) { r, c -> data[c][r] }
    fun augment(other: Matrix): Matrix {
        require(rows == other.rows) { "Row counts must match to augment" }
        return Matrix(rows, cols + other.cols) { r, c ->
            if (c < cols) data[r][c] else other.data[r][c - cols]
        }
    }

    val isSquare: Boolean get() = rows == cols
    val trace: Double
        get() {
            require(isSquare) { "Trace requires a square matrix" }
            return (0 until rows).sumOf { data[it][it] }
        }

    fun frobeniusNorm(): Double = sqrt(data.sumOf { row -> row.sumOf { it * it } })

    fun determinant(): Double {
        require(isSquare) { "Determinant requires a square matrix" }
        if (rows == 1) return data[0][0]
        if (rows == 2) return data[0][0] * data[1][1] - data[0][1] * data[1][0]

        val m = mutableCopy()
        var det = 1.0

        for (col in 0 until rows) {
            val pivotRow = (col until rows).maxByOrNull { abs(m[it][col]) }!!
            if (pivotRow != col) { swapRows(m, col, pivotRow); det *= -1 }
            val pivot = m[col][col]
            if (abs(pivot) < 1e-12) return 0.0
            det *= pivot
            for (row in col + 1 until rows) {
                val factor = m[row][col] / pivot
                for (k in col until rows) m[row][k] -= factor * m[col][k]
            }
        }
        return det
    }
    fun inverse(): Matrix {
        require(isSquare) { "Inverse requires a square matrix" }
        val n = rows
        val aug = augment(identity(n)).mutableCopy()

        for (col in 0 until n) {
            val pivotRow = (col until n).maxByOrNull { abs(aug[it][col]) }!!
            require(abs(aug[pivotRow][col]) > 1e-12) { "Matrix is singular (not invertible)" }
            swapRows(aug, col, pivotRow)

            val pivot = aug[col][col]
            for (k in 0 until 2 * n) aug[col][k] /= pivot

            for (row in 0 until n) {
                if (row == col) continue
                val factor = aug[row][col]
                for (k in 0 until 2 * n) aug[row][k] -= factor * aug[col][k]
            }
        }

        return Matrix(n, n) { r, c -> aug[r][c + n] }
    }

    fun ref(): Matrix {
        val m = mutableCopy()
        var lead = 0
        outer@ for (row in 0 until rows) {
            while (lead < cols) {
                var i = row
                while (abs(m[i][lead]) < 1e-12) {
                    if (++i == rows) { i = row; if (++lead == cols) break@outer }
                }
                swapRows(m, i, row)
                val div = m[row][lead]
                for (c in 0 until cols) m[row][c] /= div
                for (k in row + 1 until rows) {
                    val f = m[k][lead]
                    for (c in 0 until cols) m[k][c] -= f * m[row][c]
                }
                lead++
                break
            }
        }
        return Matrix(m)
    }
    fun rref(): Matrix {
        val m = ref().mutableCopy()
        for (row in rows - 1 downTo 0) {
            val pivotCol = (0 until cols).firstOrNull { abs(m[row][it]) > 1e-12 } ?: continue
            for (k in 0 until row) {
                val f = m[k][pivotCol]
                for (c in 0 until cols) m[k][c] -= f * m[row][c]
            }
        }
        return Matrix(m)
    }

    fun rank(): Int = rref().data.count { row -> row.any { abs(it) > 1e-12 } }
    fun nullity(): Int = cols - rank()

    fun luDecompose(): Pair<Matrix, Matrix> {
        require(isSquare) { "LU decomposition requires a square matrix" }
        val n = rows
        val L = mutableCopy().also { m -> for (i in 0 until n) for (j in 0 until n) m[i][j] = 0.0 }
        val U = mutableCopy()

        for (i in 0 until n) {
            L[i][i] = 1.0
            for (k in i until n) {
                U[i][k] = data[i][k] - (0 until i).sumOf { j -> L[i][j] * U[j][k] }
            }
            for (k in i + 1 until n) {
                require(abs(U[i][i]) > 1e-12) { "Zero pivot encountered; use PLU decomposition" }
                L[k][i] = (data[k][i] - (0 until i).sumOf { j -> L[k][j] * U[j][i] }) / U[i][i]
            }
        }
        return Matrix(L) to Matrix(U)
    }
    fun dominantEigenvalue(maxIter: Int = 1000, tol: Double = 1e-10): Double {
        require(isSquare) { "Eigenvalues require a square matrix" }
        var v = Vector(DoubleArray(rows) { if (it == 0) 1.0 else 0.0 })
        var lambda = 0.0
        repeat(maxIter) {
            val w = this * v
            val newL = w.magnitude
            v = w / newL
            if (abs(newL - lambda) < tol) return newL
            lambda = newL
        }
        return lambda
    }

    private fun mutableCopy(): Array<DoubleArray> = Array(rows) { r -> data[r].copyOf() }
    private fun swapRows(m: Array<DoubleArray>, a: Int, b: Int) {
        val tmp = m[a]; m[a] = m[b]; m[b] = tmp
    }
    private fun requireSameDimensions(other: Matrix, op: String) {
        require(rows == other.rows && cols == other.cols) {
            "Matrix dimensions must match for $op: ${rows}x${cols} vs ${other.rows}x${other.cols}"
        }
    }

    override fun toString(): String {
        val widths = (0 until cols).map { c ->
            (0 until rows).maxOf { r -> "%.4g".format(data[r][c]).length }
        }
        return data.joinToString("\n") { row ->
            "[ " + row.mapIndexed { c, v -> "%.4g".format(v).padStart(widths[c]) }.joinToString("  ") + " ]"
        }
    }
    fun toLaTeX(): String {
        val body = data.joinToString(" \\\\\n") { row ->
            row.joinToString(" & ") { "%.4g".format(it) }
        }
        return "\\begin{pmatrix}\n$body\n\\end{pmatrix}"
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Matrix || rows != other.rows || cols != other.cols) return false
        return data.indices.all { r -> data[r].indices.all { c -> abs(data[r][c] - other.data[r][c]) < 1e-9 } }
    }
    override fun hashCode(): Int = data.fold(0) { acc, row -> 31 * acc + row.contentHashCode() }
}