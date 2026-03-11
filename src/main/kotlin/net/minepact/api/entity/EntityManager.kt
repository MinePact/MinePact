package net.minepact.api.entity

object EntityManager {
    private val entities = mutableSetOf<CustomEntity>()

    fun add(entity: CustomEntity) {
        entities.add(entity)
    }
    fun remove(entity: CustomEntity) {
        entities.remove(entity)
    }
    fun tickAll() {
        entities.forEach { it.tick() }
    }
}