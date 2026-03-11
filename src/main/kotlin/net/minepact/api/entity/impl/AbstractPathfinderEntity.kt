package net.minepact.api.entity.impl

import net.minepact.api.entity.EntityType
import org.bukkit.Location
import org.bukkit.entity.LivingEntity

abstract class AbstractPathfinderEntity(
    type: EntityType
) : AbstractCustomEntity(type) {
    override fun createEntity(location: Location): LivingEntity {
        return createNMSEntity(location)
    }
    protected abstract fun createNMSEntity(location: Location): LivingEntity
    protected open fun registerGoals() {}
}