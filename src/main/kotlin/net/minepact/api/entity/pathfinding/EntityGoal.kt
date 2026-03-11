package net.minepact.api.entity.pathfinding

import net.minepact.api.entity.CustomEntity

interface EntityGoal {
    fun shouldStart(entity: CustomEntity): Boolean

    fun start(entity: CustomEntity) {}
    fun tick(entity: CustomEntity) {}
    fun stop(entity: CustomEntity) {}

}