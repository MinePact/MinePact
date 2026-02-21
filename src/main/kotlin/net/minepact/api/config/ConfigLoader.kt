package net.minepact.api.config

import net.minepact.Main
import org.bukkit.configuration.file.YamlConfiguration
import java.io.BufferedWriter
import java.io.File
import java.util.UUID
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

object ConfigLoader {
    fun load(config: ConfigurationFile) {
        val file = File(Main.instance.dataFolder, config.fileName)

        if (!file.exists()) {
            Main.instance.dataFolder.mkdirs()
            writeDefaultFile(file, config)
        }

        val yaml = YamlConfiguration.loadConfiguration(file)
        applyValues(yaml, "", config)
        yaml.save(file)
    }

    fun save(config: ConfigurationFile) {
        val file = File(Main.instance.dataFolder, config.fileName)
        Main.instance.dataFolder.mkdirs()

        file.bufferedWriter().use { writer ->
            writeObject(writer, config)
        }
    }

    private fun writeDefaultFile(file: File, config: ConfigurationFile) {
        file.bufferedWriter().use { writer ->
            config::class.findAnnotation<Comment>()?.let {
                writer.write("# ${it.value}\n\n")
            }

            writeObject(writer, config)
        }
    }
    private fun writeObject(
        writer: BufferedWriter,
        obj: Any,
        prefix: String = ""
    ) {
        val fields = obj::class.java.declaredFields

        for (field in fields) {
            field.isAccessible = true

            val value = field.get(obj)
            val name = field.name

            if (isSimple(value)) {
                writer.write("$prefix$name: ${serializeFlat(value)}")
                writer.newLine()
            } else if (value != null) {
                writeObject(writer, value, "$prefix$name.")
            }
        }
    }

    private fun applyValues(
        yaml: YamlConfiguration,
        path: String,
        obj: Any
    ) {
        for (prop in obj::class.memberProperties) {
            if (prop !is KMutableProperty1<*, *>) continue

            val key = if (path.isEmpty()) prop.name else "$path.${prop.name}"
            val current = prop.getter.call(obj)

            if (isSimple(current)) {
                if (yaml.contains(key)) {
                    try {
                        val raw = yaml.get(key)

                        val valueToSet = when (current) {
                            is UUID -> {
                                val s = yaml.getString(key) ?: raw?.toString()
                                if (s != null) UUID.fromString(s) else raw as? UUID
                            }
                            else -> raw
                        }

                        if (valueToSet != null) {
                            prop.setter.call(obj, valueToSet)
                        }
                    } catch (_: Exception) { }
                } else {
                    yaml.set(key, current)
                }
            } else {
                applyValues(yaml, key, current!!)
            }
        }
    }

    private fun serialize(value: Any?): String =
        when (value) {
            null -> "null"
            is String -> "\"$value\""
            is Collection<*> -> {
                if (value.isEmpty()) "[]"
                else value.joinToString(prefix = "[", postfix = "]") { serialize(it) }
            }
            is Map<*, *> -> {
                if (value.isEmpty()) "{}"
                else value.entries.joinToString(prefix = "{", postfix = "}") {
                    "${serialize(it.key)}: ${serialize(it.value)}"
                }
            }
            is UUID -> { "\"$value\"" }
            else -> value.toString()
        }
    private fun serializeFlat(value: Any?): String =
        when (value) {
            null -> "null"
            is UUID -> value.toString()
            else -> value.toString()
        }

    private fun isSimple(value: Any?): Boolean =
        value == null ||
                value is String ||
                value is Number ||
                value is Boolean ||
                value is Enum<*> ||
                value is Collection<*> ||
                value is Map<*, *> ||
                value is UUID
}
