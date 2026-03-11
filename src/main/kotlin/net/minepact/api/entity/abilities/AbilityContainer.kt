package net.minepact.api.entity.abilities

import net.minepact.api.entity.CustomEntity

class AbilityContainer {
    private val abilities = mutableListOf<EntityAbility>()

    fun add(ability: EntityAbility) {
        abilities.add(ability)
    }

    fun tick(entity: CustomEntity) {
        abilities.forEach { it.onTick(entity) }
    }
    fun damage(entity: CustomEntity, damage: Double) {
        abilities.forEach { it.onDamage(entity, damage) }
    }
    fun death(entity: CustomEntity) {
        abilities.forEach { it.onDeath(entity) }
    }
}