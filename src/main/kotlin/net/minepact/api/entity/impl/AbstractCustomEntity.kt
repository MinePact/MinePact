package net.minepact.api.entity.impl

import net.minepact.api.entity.CustomEntity
import net.minepact.api.entity.EntityType
import net.minepact.api.entity.abilities.AbilityContainer
import net.minepact.api.entity.pathfinding.GoalSelector
import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import java.util.*

abstract class AbstractCustomEntity(
    final override val type: EntityType
) : CustomEntity {
    final override val id: UUID = UUID.randomUUID()
    final override lateinit var entity: LivingEntity
    val abilities = AbilityContainer()
    val goals = GoalSelector()

    override fun spawn(location: Location) {
        entity = createEntity(location)
        onSpawn()
    }
    override fun despawn() {
        entity.remove()
        onDespawn()
    }
    override fun tick() {
        goals.tick(this)
        abilities.tick(this)
        onTick()
    }

    protected abstract fun createEntity(location: Location): LivingEntity
    protected open fun onSpawn() {}
    protected open fun onTick() {}
    protected open fun onDespawn() {}
}