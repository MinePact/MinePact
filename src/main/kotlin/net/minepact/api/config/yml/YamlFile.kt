package net.minepact.api.config.yml

import net.minepact.api.config.FileReader
import net.minepact.api.config.FileWriter
import java.io.File

class YamlFile(
    val file: File
) {
    private val reader: FileReader<YamlSection> = YamlFileReader(file)
    private val writer: FileWriter<YamlSection> = YamlFileWriter(file)
    var root: YamlSection = reader.read()
        private set

    fun reload() {
        root = reader.read()
    }
    fun save() {
        writer.write(root)
    }

    inline fun <reified T> get(path: String): T? {
        val obj = resolve(path) ?: return null

        return when (obj) {
            is YamlPrimitive<*> -> obj.value as? T
            is YamlList<*> -> obj.value as? T
            else -> null
        }
    }
    fun set(path: String, value: Any) {
        val parts = path.split(".")
        var current = root

        for (i in 0 until parts.size - 1) {
            val key = parts[i]
            val next = current.get(key)

            if (next is YamlSection) {
                current = next
            } else {
                val newSection = YamlSection(key)
                current.set(newSection)
                current = newSection
            }
        }

        val finalKey = parts.last()
        current.set(YamlPrimitive(finalKey, value))
    }

    fun resolve(path: String): YamlObject<*>? {
        val parts = path.split(".")
        var current: YamlObject<*> = root

        for (part in parts) {
            if (current !is YamlSection) return null
            current = current.get(part) ?: return null
        }

        return current
    }
}