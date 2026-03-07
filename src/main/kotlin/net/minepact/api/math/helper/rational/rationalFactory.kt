package net.minepact.api.math.helper.rational

import net.minepact.api.math.Rational

fun rat(n: Int, d: Int = 1) = Rational.Companion(n, d)
fun rat(n: Long, d: Long = 1L) = Rational.Companion(n, d)