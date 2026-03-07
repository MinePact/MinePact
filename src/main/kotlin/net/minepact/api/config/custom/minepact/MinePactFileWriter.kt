package net.minepact.api.config.custom.minepact

import net.minepact.api.config.custom.ConfigValue
import net.minepact.api.config.custom.interfaces.FileWriter
import net.minepact.api.config.custom.exception.ConfigException
import java.io.File
import kotlin.collections.iterator

class MinePactFileWriter(
    private val data:   MutableMap<String, ConfigValue>,
    private val file: File,
    private val parser: MinePactFileParser = MinePactFileParser()
) : FileWriter {
    override fun set(path: String, value: String)  { data[path] = ConfigValue.StringValue(value) }
    override fun set(path: String, value: Int)     { data[path] = ConfigValue.IntValue(value)    }
    override fun set(path: String, value: Double)  { data[path] = ConfigValue.DoubleValue(value) }
    override fun set(path: String, value: Float)   { data[path] = ConfigValue.FloatValue(value)  }
    override fun set(path: String, value: Boolean) { data[path] = ConfigValue.BoolValue(value)   }

    override fun <T> setList(path: String, values: List<T>) {
        data[path] = ConfigValue.ListValue(values.map { it.toConfigValue() })
    }
    override fun <K, V> setMap(path: String, entries: Map<K, V>) {
        data[path] = ConfigValue.MapValue(
            entries.map { (k, v) -> k.toConfigValue() to v.toConfigValue() }
        )
    }
    override fun remove(path: String) { data.remove(path) }
    override fun contains(path: String): Boolean = data.containsKey(path)

    override fun save() {
        file.parentFile?.mkdirs()
        file.writeText(serialise())
    }
    override fun reload() {
        if (!file.exists()) return
        val fresh = parser.parse(file.readText())
        data.clear()
        data.putAll(fresh)
    }

    internal fun serialise(): String {
        // Build a tree: Map<String, Any> where leaves are ConfigValue
        val tree: MutableMap<String, Any> = mutableMapOf()
        for ((dotPath, value) in data.entries.sortedBy { it.key }) {
            val segments = dotPath.split('.')
            insertIntoTree(tree, segments, value)
        }
        val sb = StringBuilder()
        writeNode(sb, tree, indent = 0)
        return sb.toString().trimEnd() + "\n"
    }

    @Suppress("UNCHECKED_CAST") private fun insertIntoTree(
        node:     MutableMap<String, Any>,
        segments: List<String>,
        value: ConfigValue
    ) {
        if (segments.size == 1) {
            node[segments[0]] = value
            return
        }
        val child = node.getOrPut(segments[0]) { mutableMapOf<String, Any>() }
        if (child !is MutableMap<*, *>)
            throw ConfigException("Path conflict: '${segments[0]}' is both a value and a parent key")
        insertIntoTree(child as MutableMap<String, Any>, segments.drop(1), value)
    }
    @Suppress("UNCHECKED_CAST") private fun writeNode(
        sb:     StringBuilder,
        node:   Map<String, Any>,
        indent: Int
    ) {
        val pad = "    ".repeat(indent)
        for ((key, child) in node) {
            when (child) {
                is Map<*, *>                 -> {
                    sb.appendLine("$pad$key:")
                    writeNode(sb, child as Map<String, Any>, indent + 1)
                }
                is ConfigValue.ListValue     -> {
                    sb.appendLine("$pad$key:")
                    child.items.forEach { sb.appendLine("$pad    - ${it.toMpcString()}") }
                }
                is ConfigValue.MapValue      -> {
                    sb.appendLine("$pad$key: ${child.toMpcString()}")
                }
                is ConfigValue -> {
                    sb.appendLine("$pad$key: ${child.toMpcString()}")
                }
            }
        }
    }

    private fun ConfigValue.toMpcString(): String = when (this) {
        is ConfigValue.StringValue  -> if (value.contains(' ') || value.isEmpty()) "\"$value\"" else value
        is ConfigValue.IntValue     -> value.toString()
        is ConfigValue.DoubleValue  -> value.toString()
        is ConfigValue.FloatValue   -> "${value}f"
        is ConfigValue.BoolValue    -> value.toString()
        is ConfigValue.ListValue    -> items.joinToString(", ", "[", "]") { it.toMpcString() }
        is ConfigValue.MapValue     -> entries.joinToString(", ", "[", "]") { (k, v) ->
            "{${k.toMpcString()}, ${v.toMpcString()}}"
        }
    }
    private fun Any?.toConfigValue(): ConfigValue = when (this) {
        is String  -> ConfigValue.StringValue(this)
        is Int     -> ConfigValue.IntValue(this)
        is Double  -> ConfigValue.DoubleValue(this)
        is Float   -> ConfigValue.FloatValue(this)
        is Boolean -> ConfigValue.BoolValue(this)
        else       -> ConfigValue.StringValue(this.toString())
    }
}