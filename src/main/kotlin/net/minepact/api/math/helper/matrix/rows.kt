package net.minepact.api.math.helper.matrix

fun row(vararg values: Double): DoubleArray = values
fun row(vararg values: Int): DoubleArray = values.map { it.toDouble() }.toDoubleArray()