package net.minepact.api.config.yml

import net.minepact.api.config.FileReader
import java.io.File

class YamlFileReader(
    private val file: File
) : FileReader<YamlSection> {
    override fun read(): YamlSection {
        if (!file.exists()) {
            file.parentFile?.mkdirs()
            file.createNewFile()
        }
        val root = YamlSection("root")

        val lines = file.readLines()
        val sectionStack = ArrayDeque<YamlSection>()
        val indentStack = ArrayDeque<Int>()

        sectionStack.add(root)
        indentStack.add(0)

        var currentList: YamlList<Any>? = null

        for (rawLine in lines) {
            if (rawLine.isBlank() || rawLine.trimStart().startsWith("#"))
                continue

            val indent = rawLine.indexOfFirst { !it.isWhitespace() }
            val line = rawLine.trim()

            while (indent < indentStack.last()) {
                sectionStack.removeLast()
                indentStack.removeLast()
            }

            val currentSection = sectionStack.last()

            if (line.startsWith("- ")) {
                val valuePart = line.removePrefix("- ").trim()
                val parsedValue = parsePrimitive(valuePart)

                if (currentList == null) {
                    currentList = YamlList("list", mutableListOf())
                }

                currentList.value.add(parsedValue)
                continue
            }

            currentList = null

            val parts = line.split(":", limit = 2)
            if (parts.isEmpty()) continue

            val key = parts[0].trim()
            val valuePart = parts.getOrNull(1)?.trim()

            if (valuePart.isNullOrEmpty()) {
                val newSection = YamlSection(key)
                currentSection.set(newSection)

                sectionStack.add(newSection)
                indentStack.add(indent)
            } else if (valuePart == "[]") {
                val list = YamlList<Any>(key, mutableListOf())
                currentSection.set(list)
            } else {
                val parsedValue = parsePrimitive(valuePart)
                val primitive = YamlPrimitive(key, parsedValue)
                currentSection.set(primitive)
            }
        }

        return root
    }

    private fun parsePrimitive(value: String): Any {
        return when {
            value.equals("true", true) -> true
            value.equals("false", true) -> false
            value.toIntOrNull() != null -> value.toInt()
            value.toDoubleOrNull() != null -> value.toDouble()
            value.startsWith("\"") && value.endsWith("\"") -> value.removeSurrounding("\"")
            else -> value
        }
    }
}