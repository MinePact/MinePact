package net.minepact.api.entity

import org.bukkit.Location

object EntityFactory {
    fun spawn(key: String, loc: Location): CustomEntity? {
        val entity = EntityRegistry.create(key) ?: return null
        entity.spawn(loc)
        return entity
    }
}