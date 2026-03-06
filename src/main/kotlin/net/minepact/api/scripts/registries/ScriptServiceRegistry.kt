package net.minepact.api.scripts.registries

import kotlin.reflect.KClass

class ScriptServiceRegistry {
    private val byType = mutableMapOf<KClass<*>, Any>()
    fun <T : Any> register(type: KClass<T>, instance: T) {
        byType[type] = instance
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T : Any> get(type: KClass<T>): T? = byType[type] as? T
    fun clear() = byType.clear()
    fun registeredNames(): List<String> = byType.keys.map { it.simpleName ?: it.toString() }
}