package net.minepact.api.math.helper.vector

import net.minepact.api.math.Vector

operator fun Double.times(v: Vector): Vector = v * this
operator fun Int.times(v: Vector): Vector = v * this