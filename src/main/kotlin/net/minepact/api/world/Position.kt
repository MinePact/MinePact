package net.minepact.api.world

import net.minepact.api.math.Vector
import net.minepact.api.math.helper.vector.vec
import kotlin.math.floor

class Position(
    val vector: Vector,
    val yaw: Float = 0f,
    val pitch: Float = 0f,
    val world: String = "world"
) {
    val x: Double get() = vector.x
    val y: Double get() = vector.y
    val z: Double get() = vector.z
    val blockX: Int get() = floor(x).toInt()
    val blockY: Int get() = floor(y).toInt()
    val blockZ: Int get() = floor(z).toInt()

    companion object {
        operator fun invoke(
            x: Double, y: Double, z: Double,
            yaw:   Float  = 0f,
            pitch: Float  = 0f,
            world: String = "world"
        ) = Position(vec(x, y, z), yaw, pitch, world)

        fun spawn(world: String = "world") = Position(0.0, 64.0, 0.0, world = world)
        fun origin(world: String = "world") = Position(0.0, 0.0, 0.0, world = world)

        fun fromBukkit(loc: org.bukkit.Location): Position =
            Position(loc.x, loc.y, loc.z, loc.yaw, loc.pitch, loc.world?.name ?: "world")
    }

    fun asBukkitLocation(): org.bukkit.Location = org.bukkit.Location(org.bukkit.Bukkit.getWorld(world), x, y, z, yaw, pitch)
    fun copy(
        x:     Double = this.x,
        y:     Double = this.y,
        z:     Double = this.z,
        yaw:   Float  = this.yaw,
        pitch: Float  = this.pitch,
        world: String = this.world
    ): Position = Position(x, y, z, yaw, pitch, world)

    fun asMinePactWorld(): World? = WorldManager.get(world)

    fun distanceTo(other: Position): Double = vector.distanceTo(other.vector)
    fun distanceSquaredTo(other: Position): Double = (vector - other.vector).magnitudeSquared
    fun directionTo(other: Position): Vector = (other.vector - vector).normalized()

    operator fun plus(offset: Vector): Position = Position(vector + offset, yaw, pitch, world)
    operator fun minus(offset: Vector): Position = Position(vector - offset, yaw, pitch, world)
    operator fun plus(other: Position): Position = Position(vector + other.vector, yaw, pitch, world)

    fun midpointTo(other: Position): Position = Position((vector + other.vector) / 2.0, world = world)
    fun isWithinRadius(other: Position, radius: Double): Boolean = distanceSquaredTo(other) <= radius * radius
    fun isSameBlock(other: Position): Boolean = blockX == other.blockX && blockY == other.blockY && blockZ == other.blockZ
    fun isSameWorld(other: Position): Boolean = world == other.world

    fun lookDirection(): Vector {
        val yawRad   = Math.toRadians(yaw.toDouble())
        val pitchRad = Math.toRadians(pitch.toDouble())
        val xz = kotlin.math.cos(pitchRad)
        return vec(
            -xz * kotlin.math.sin(yawRad),
            -kotlin.math.sin(pitchRad),
            xz * kotlin.math.cos(yawRad)
        )
    }

    fun inFront(distance: Double): Position = Position(vector + lookDirection() * distance, yaw, pitch, world)

    override fun toString() = "Position(world=$world, x=$x, y=$y, z=$z, yaw=$yaw, pitch=$pitch)"
    fun toShortString() = "($blockX, $blockY, $blockZ) @ $world"

    override fun equals(other: Any?): Boolean {
        if (other !is Position) return false
        return vector == other.vector && world == other.world
    }
    override fun hashCode(): Int = 31 * vector.hashCode() + world.hashCode()
}