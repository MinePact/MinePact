package net.minepact.api.entity

import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import java.util.UUID

interface CustomEntity {
    val id: UUID
    val type: EntityType
    val entity: LivingEntity

    fun spawn(location: Location)
    fun despawn()

    fun tick()

}