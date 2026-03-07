package net.minepact.api.math.helper.vector

import net.minepact.api.math.Vector

fun vec(vararg components: Double): Vector = Vector.of(*components)
fun vec(vararg components: Int): Vector = Vector.of(*components)