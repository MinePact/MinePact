package net.minepact.api.entity.dsl

import net.minepact.api.entity.abilities.EntityAbility
import net.minepact.api.entity.pathfinding.EntityGoal

class EntityBuilder {
    var displayName: String = "Custom Entity"
    var health: Double = 20.0

    val abilities = mutableListOf<EntityAbility>()
    val goals = mutableListOf<EntityGoal>()

    fun ability(ability: EntityAbility) {
        abilities.add(ability)
    }
    fun goal(goal: EntityGoal) {
        goals.add(goal)
    }
}