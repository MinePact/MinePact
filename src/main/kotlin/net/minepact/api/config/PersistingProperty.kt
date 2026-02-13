package net.minepact.api.config

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class PersistingProperty<T>(initial: T) : ReadWriteProperty<Any?, T> {
    private var field: T = initial

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = field
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        field = value
        if (thisRef is ConfigurationFile) {
            @Suppress("UNCHECKED_CAST")
            val clazz = thisRef::class as KClass<ConfigurationFile>
            ConfigurationRegistry.save(clazz)
        }
    }
}