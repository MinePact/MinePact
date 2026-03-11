package net.minepact.api.entity.pathfinding

import net.minepact.api.entity.CustomEntity

class GoalSelector {
    private val goals = mutableListOf<EntityGoal>()

    fun add(goal: EntityGoal) {
        goals.add(goal)
    }

    fun tick(entity: CustomEntity) {
        goals.forEach {
            if (it.shouldStart(entity)) {
                it.start(entity)
                it.tick(entity)
            }
        }
    }
}