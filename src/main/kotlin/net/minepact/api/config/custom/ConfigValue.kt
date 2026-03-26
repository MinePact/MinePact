package net.minepact.api.config.custom

import net.minepact.api.config.custom.exception.ConfigException
import net.minepact.api.config.custom.exception.ConfigListTypeMismatchException
import net.minepact.api.config.custom.exception.ConfigMapTypeMismatchException
import kotlin.reflect.KClass
import kotlin.reflect.KType

sealed class ConfigValue {
    abstract fun asString(): String
    abstract fun asInt(): Int
    abstract fun asDouble(): Double
    abstract fun asFloat(): Float
    abstract fun asBoolean(): Boolean

    fun asList(path: String, elementType: KType): List<Any?> {
        if (this !is ListValue) throw ConfigListTypeMismatchException(path, "List", typeName(), -1)

        return items.mapIndexed { index, item ->
            try {
                item.coerceTo(elementType.classifier as? KClass<*>)
            } catch (e: ConfigException) {
                throw ConfigListTypeMismatchException(
                    path,
                    elementType.classifier.toString(),
                    item.typeName(),
                    index
                )
            }
        }
    }
    fun asMap(path: String, keyType: KType, valueType: KType): Map<Any?, Any?> {
        if (this !is MapValue) throw ConfigMapTypeMismatchException(path, "value", "Map", typeName())

        val keyCls = keyType.classifier as? KClass<*>
        val valCls = valueType.classifier as? KClass<*>

        return entries.associate { (rawKey, rawVal) ->
            val k = try {
                rawKey.coerceTo(keyCls)
            } catch (e: ConfigException) {
                throw ConfigMapTypeMismatchException(path, "key", keyCls.toString(), rawKey.typeName())
            }
            val v = try {
                rawVal.coerceTo(valCls)
            } catch (e: ConfigException) {
                throw ConfigMapTypeMismatchException(path, "value", valCls.toString(), rawVal.typeName())
            }
            k to v
        }
    }

    internal fun coerceTo(cls: KClass<*>?): Any = when (cls) {
        String::class -> asString()
        Int::class -> asInt()
        Double::class -> asDouble()
        Float::class -> asFloat()
        Boolean::class -> asBoolean()
        null -> asString()
        else -> throw ConfigException("Cannot coerce ${typeName()} to $cls")
    }
    internal abstract fun typeName(): String
    
    data class StringValue(val value: String) : ConfigValue() {
        override fun asString() = value
        override fun asInt() = value.toIntOrNull() ?: throw ConfigException("Cannot parse '$value' as Int")
        override fun asDouble() = value.toDoubleOrNull() ?: throw ConfigException("Cannot parse '$value' as Double")
        override fun asFloat() = value.toFloatOrNull() ?: throw ConfigException("Cannot parse '$value' as Float")
        override fun asBoolean() = when (value.lowercase()) {
            "true" -> true
            "false" -> false
            else -> throw ConfigException("Cannot parse '$value' as Boolean")
        }
        override fun typeName() = "String"
        override fun toString() = "\"$value\""
    }
    data class IntValue(val value: Int) : ConfigValue() {
        override fun asString() = value.toString()
        override fun asInt() = value
        override fun asDouble() = value.toDouble()
        override fun asFloat() = value.toFloat()
        override fun asBoolean() = value != 0
        override fun typeName() = "Int"
        override fun toString() = value.toString()
    }
    data class DoubleValue(val value: Double) : ConfigValue() {
        override fun asString() = value.toString()
        override fun asInt() = value.toInt()
        override fun asDouble() = value
        override fun asFloat() = value.toFloat()
        override fun asBoolean() = value != 0.0
        override fun typeName() = "Double"
        override fun toString() = value.toString()
    }
    data class FloatValue(val value: Float) : ConfigValue() {
        override fun asString() = value.toString()
        override fun asInt() = value.toInt()
        override fun asDouble() = value.toDouble()
        override fun asFloat() = value
        override fun asBoolean() = value != 0f
        override fun typeName() = "Float"
        override fun toString() = "${value}f"
    }
    data class BoolValue(val value: Boolean) : ConfigValue() {
        override fun asString() = value.toString()
        override fun asInt() = if (value) 1 else 0
        override fun asDouble() = if (value) 1.0 else 0.0
        override fun asFloat() = if (value) 1f else 0f
        override fun asBoolean() = value
        override fun typeName() = "Boolean"
        override fun toString() = value.toString()
    }

    data class ListValue(val items: List<ConfigValue>) : ConfigValue() {
        override fun asString() = items.joinToString(", ", "[", "]") { it.asString() }
        override fun asInt() = throw ConfigException("Cannot convert List to Int")
        override fun asDouble() = throw ConfigException("Cannot convert List to Double")
        override fun asFloat() = throw ConfigException("Cannot convert List to Float")
        override fun asBoolean() = throw ConfigException("Cannot convert List to Boolean")
        override fun typeName() = "List"
        override fun toString() = items.joinToString(", ", "[", "]")
    }
    data class MapValue(val entries: List<Pair<ConfigValue, ConfigValue>>) : ConfigValue() {
        override fun asString() = entries.joinToString(", ", "[", "]") { (k, v) -> "{${k.asString()}, ${v.asString()}}" }
        override fun asInt() = throw ConfigException("Cannot convert Map to Int")
        override fun asDouble() = throw ConfigException("Cannot convert Map to Double")
        override fun asFloat() = throw ConfigException("Cannot convert Map to Float")
        override fun asBoolean() = throw ConfigException("Cannot convert Map to Boolean")
        override fun typeName() = "Map"
        override fun toString() = entries.joinToString(", ", "[", "]") { (k, v) -> "{$k, $v}" }
    }
}