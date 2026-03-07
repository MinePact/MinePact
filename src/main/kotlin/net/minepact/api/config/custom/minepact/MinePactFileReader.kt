package net.minepact.api.config.custom.minepact

import net.minepact.api.config.custom.ConfigValue
import net.minepact.api.config.custom.interfaces.FileReader
import net.minepact.api.config.custom.exception.ConfigException
import net.minepact.api.config.custom.exception.ConfigKeyNotFoundException
import kotlin.reflect.KClass
import kotlin.reflect.KType

class MinePactFileReader(private val data: Map<String, ConfigValue>) : FileReader {
    @Suppress("UNCHECKED_CAST")
    override fun <T> get(path: String, type: KType): T {
        val value = data[path] ?: throw ConfigKeyNotFoundException(path)

        return when (val cls = type.classifier as? KClass<*>) {
            String::class -> value.asString() as T
            Int::class -> value.asInt() as T
            Double::class -> value.asDouble() as T
            Float::class -> value.asFloat() as T
            Boolean::class -> value.asBoolean() as T

            List::class -> {
                val elementType = type.arguments.firstOrNull()?.type
                    ?: throw ConfigException("get<List<T>> requires a type argument, e.g. get<List<Int>>(\"$path\")")
                value.asList(path, elementType) as T
            }

            Map::class -> {
                val keyType = type.arguments.getOrNull(0)?.type
                    ?: throw ConfigException("get<Map<K,V>> requires key type argument at '$path'")
                val valType = type.arguments.getOrNull(1)?.type
                    ?: throw ConfigException("get<Map<K,V>> requires value type argument at '$path'")
                value.asMap(path, keyType, valType) as T
            }

            else -> throw ConfigException(
                    "Unsupported config type '$cls' at path '$path'. " +
                    "Supported types: String, Int, Double, Float, Boolean, List<T>, Map<K,V>"
            )
        }
    }

    override fun contains(path: String): Boolean = data.containsKey(path)
    override fun keys(): Set<String> = data.keys.map { it.substringBefore('.') }.toSet()
    override fun allPaths(): Set<String> = data.keys.toSet()
    override fun raw(path: String): ConfigValue? = data[path]
}