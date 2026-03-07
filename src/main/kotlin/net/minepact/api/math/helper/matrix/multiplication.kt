package net.minepact.api.math.helper.matrix

import net.minepact.api.math.Matrix

operator fun Double.times(m: Matrix): Matrix = m * this
operator fun Int.times(m: Matrix): Matrix = m * this