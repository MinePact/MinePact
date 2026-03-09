package net.minepact.api.config.yml

import net.minepact.api.config.FileWriter
import java.io.File

class YamlFileWriter(
    private val file: File
) : FileWriter<YamlSection> {
    override fun write(data: YamlSection) {
        file.parentFile?.mkdirs()

        val yamlText = buildString { writeSection(data, 0) }

        file.writeText(yamlText)
    }

    private fun writeSection(section: YamlSection, indent: Int, builder: StringBuilder = StringBuilder()) {
        section.value.forEach { (_, obj) ->
            writeObject(obj, indent, builder)
        }
    }
    private fun writeObject(obj: YamlObject<*>, indent: Int, builder: StringBuilder) {
        val indentStr = "  ".repeat(indent)

        when (obj) {
            is YamlPrimitive<*> -> {
                builder.appendLine("$indentStr${obj.identifier}: ${formatValue(obj.value)}")
            }

            is YamlList<*> -> {
                builder.appendLine("$indentStr${obj.identifier}:")
                obj.value.forEach {
                    builder.appendLine("${indentStr}  - ${formatValue(it)}")
                }
            }

            is YamlSection -> {
                builder.appendLine("$indentStr${obj.identifier}:")
                obj.value.forEach { (_, child) ->
                    writeObject(child, indent + 1, builder)
                }
            }
        }
    }

    private fun formatValue(value: Any?): String {
        return when (value) {
            is String -> {
                if (value.contains(" "))
                    "\"$value\""
                else value
            }
            else -> value.toString()
        }
    }
}