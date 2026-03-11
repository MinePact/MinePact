package net.minepact.api.entity.abilities

import net.minepact.api.entity.CustomEntity

interface EntityAbility {
    val key: String

    fun onAttach(entity: CustomEntity) {}
    fun onTick(entity: CustomEntity) {}
    fun onDamage(entity: CustomEntity, damage: Double) {}
    fun onDeath(entity: CustomEntity) {}
}