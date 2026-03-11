package net.minepact.core.global.entity

import net.minepact.api.entity.CustomEntity
import net.minepact.api.entity.EntityType

enum class EntityTypes : EntityType {
    DIAMOND_ZOMBIE {
        override val key: String = "diamond_zombie"
        override val displayName: String = "Diamond Zombie"
        override fun create(): CustomEntity = DiamondZombieEntity(this)
    }
}