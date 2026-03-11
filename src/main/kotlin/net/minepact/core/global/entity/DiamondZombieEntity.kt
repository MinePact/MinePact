package net.minepact.core.global.entity

import net.minepact.api.entity.EntityType
import net.minepact.api.entity.impl.AbstractPathfinderEntity
import org.bukkit.Location
import org.bukkit.entity.LivingEntity

class DiamondZombieEntity(
    type: EntityType
) : AbstractPathfinderEntity(type) {

    override fun createNMSEntity(location: Location): LivingEntity {
        val world = location.world!!

        val zombie = world.spawn(location, org.bukkit.entity.Zombie::class.java)

        zombie.customName = type.displayName
        zombie.isCustomNameVisible = true

        zombie.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH)?.baseValue = 40.0
        zombie.health = 40.0

        return zombie
    }
}