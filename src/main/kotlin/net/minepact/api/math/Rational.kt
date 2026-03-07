package net.minepact.api.math

import java.math.BigInteger

class Rational private constructor(
    val numerator: BigInteger,
    val denominator: BigInteger
) : Number(), Comparable<Rational> {

    companion object {
        val ZERO = Rational(BigInteger.ZERO, BigInteger.ONE)
        val ONE = Rational(BigInteger.ONE,  BigInteger.ONE)
        val TWO = Rational(BigInteger.TWO,  BigInteger.ONE)
        val NEGATIVE_ONE = Rational(-BigInteger.ONE, BigInteger.ONE)

        operator fun invoke(n: BigInteger, d: BigInteger = BigInteger.ONE): Rational {
            require(d != BigInteger.ZERO) { "Denominator cannot be zero" }
            
            val gcd = n.gcd(d)
            val sign = if (d.signum() < 0) BigInteger.ONE.negate() else BigInteger.ONE
            return Rational(n.divide(gcd).multiply(sign), d.abs().divide(gcd))
        }
        operator fun invoke(n: Long, d: Long = 1L): Rational = invoke(BigInteger.valueOf(n), BigInteger.valueOf(d))
        operator fun invoke(n: Int, d: Int = 1): Rational = invoke(n.toLong(), d.toLong())

        fun approx(value: Double, maxDenominator: Long = 1_000_000L): Rational {
            return if (value == kotlin.math.floor(value) && value.isFinite()) invoke(value.toLong())
            else invoke((value * maxDenominator).toLong(), maxDenominator)
        }
    }

    operator fun plus(other: Rational) = invoke(numerator * other.denominator + other.numerator * denominator, denominator * other.denominator)
    operator fun minus(other: Rational) = invoke(numerator * other.denominator - other.numerator * denominator, denominator * other.denominator)
    operator fun times(other: Rational) = invoke(numerator * other.numerator, denominator * other.denominator)

    operator fun div(other: Rational): Rational {
        require(!other.isZero()) { "Division by zero" }
        return invoke(numerator * other.denominator, denominator * other.numerator)
    }

    operator fun unaryMinus(): Rational = Rational(numerator.negate(), denominator)
    operator fun unaryPlus(): Rational = this

    fun pow(exp: Int): Rational = when {
        exp == 0 -> ONE
        exp > 0 -> invoke(numerator.pow(exp), denominator.pow(exp))
        else -> invoke(denominator.pow(-exp), numerator.pow(-exp))
    }

    fun abs(): Rational = if (numerator.signum() < 0) -this else this
    fun reciprocal(): Rational = invoke(denominator, numerator)

    operator fun plus(n: Int) = this + invoke(n)
    operator fun minus(n: Int) = this - invoke(n)
    operator fun times(n: Int) = this * invoke(n)
    operator fun div(n: Int) = this / invoke(n)

    operator fun plus(n: Long) = this + invoke(n)
    operator fun minus(n: Long) = this - invoke(n)
    operator fun times(n: Long) = this * invoke(n)
    operator fun div(n: Long) = this / invoke(n)

    fun isZero() = numerator == BigInteger.ZERO
    fun isOne() = numerator == denominator
    fun isInteger() = denominator == BigInteger.ONE
    fun isPositive() = numerator.signum() > 0
    fun isNegative() = numerator.signum() < 0

    override fun toDouble() = numerator.toDouble() / denominator.toDouble()
    override fun toFloat() = toDouble().toFloat()
    override fun toInt() = numerator.divide(denominator).toInt()
    override fun toLong() = numerator.divide(denominator).toLong()
    override fun toByte() = toLong().toByte()
    override fun toShort()= toLong().toShort()

    override fun compareTo(other: Rational): Int = (numerator * other.denominator).compareTo(other.numerator * denominator)
    override fun equals(other: Any?) = when (other) {
        is Rational -> numerator == other.numerator && denominator == other.denominator
        is Int -> isInteger() && numerator == BigInteger.valueOf(other.toLong())
        is Long -> isInteger() && numerator == BigInteger.valueOf(other)
        else -> false
    }
    override fun hashCode() = 31 * numerator.hashCode() + denominator.hashCode()

    override fun toString() = if (isInteger()) "$numerator" else "$numerator/$denominator"
    fun toLaTeX() = if (isInteger()) "$numerator" else "\\frac{$numerator}{$denominator}"
}