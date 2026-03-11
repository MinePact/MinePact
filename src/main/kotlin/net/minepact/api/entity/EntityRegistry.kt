package net.minepact.api.entity

object EntityRegistry {
    private val types = mutableMapOf<String, EntityType>()

    fun register(type: EntityType) {
        types[type.key] = type
    }
    fun get(key: String): EntityType? {
        return types[key]
    }
    fun create(key: String): CustomEntity? {
        return types[key]?.create()
    }
}