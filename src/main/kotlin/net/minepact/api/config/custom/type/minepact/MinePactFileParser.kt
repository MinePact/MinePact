package net.minepact.api.config.custom.type.minepact

import net.minepact.api.config.custom.ConfigValue
import net.minepact.api.config.custom.interfaces.FileParser
import net.minepact.api.config.custom.exception.ConfigParseException
import kotlin.text.iterator

class MinePactFileParser : FileParser {
    override fun parse(content: String): Map<String, ConfigValue> {
        val result = mutableMapOf<String, ConfigValue>()
        val lines = content.lines()

        data class StackEntry(val indent: Int, val key: String)
        val stack = ArrayDeque<StackEntry>()

        val listItems = mutableListOf<String>()
        var listOwnerPath = ""
        var listOwnerIndent = -1

        fun currentPrefix(): String =
            stack.joinToString(".") { it.key }
        fun resolvePath(key: String): String {
            val prefix = currentPrefix()
            return if (prefix.isEmpty()) key else "$prefix.$key"
        }
        fun flushList() {
            if (listItems.isEmpty() || listOwnerPath.isEmpty()) return
            result[listOwnerPath] = ConfigValue.ListValue(listItems.map { parseScalar(it) })
            listItems.clear()
            listOwnerPath = ""
            listOwnerIndent = -1
        }

        lines.forEachIndexed { lineIndex, rawLine ->
            val lineNo = lineIndex + 1
            val trimEnd = rawLine.trimEnd()

            if (trimEnd.isBlank()) return@forEachIndexed
            val stripped = trimEnd.trimStart()
            if (stripped.startsWith("#")) return@forEachIndexed

            val indent = rawLine.length - stripped.length
            if (stripped.startsWith("- ")) {
                val itemRaw = stripped.removePrefix("- ").trim().stripInlineComment()
                if (listItems.isEmpty()) {
                    listOwnerPath = currentPrefix()
                    listOwnerIndent = stack.lastOrNull()?.indent ?: -1
                }
                listItems.add(itemRaw)
                return@forEachIndexed
            }

            flushList()
            while (stack.isNotEmpty() && stack.last().indent >= indent) {
                stack.removeLast()
            }

            val colonIdx = stripped.indexOf(':')
            if (colonIdx == -1)
                throw ConfigParseException("Expected 'key: value' but found: $stripped", lineNo)

            val rawKey = stripped.take(colonIdx).trim()
            val rawValue = stripped.substring(colonIdx + 1).trim().stripInlineComment()
            val fullPath = resolvePath(rawKey)

            when {
                rawValue.isEmpty() -> { stack.addLast(StackEntry(indent, rawKey)) }
                rawValue.startsWith("[") && rawValue.contains('{') -> { result[fullPath] = parseInlineMap(rawValue, lineNo) }
                rawValue.startsWith("[") -> { result[fullPath] = parseInlineList(rawValue, lineNo) }
                else -> { result[fullPath] = parseScalar(rawValue) }
            }
        }

        flushList()
        return result
    }
    fun parseScalar(raw: String): ConfigValue {
        val s = raw.trim()
        if (s.isEmpty()) return ConfigValue.StringValue("")
        if (s.length >= 2 && s.startsWith('"') && s.endsWith('"')) return ConfigValue.StringValue(s.substring(1, s.length - 1))

        if (s.equals("true",  ignoreCase = true)) return ConfigValue.BoolValue(true)
        if (s.equals("false", ignoreCase = true)) return ConfigValue.BoolValue(false)
        if (s.endsWith('f') || s.endsWith('F')) {
            val f = s.dropLast(1).toFloatOrNull()
            if (f != null) return ConfigValue.FloatValue(f)
        }
        s.toIntOrNull()?.let  { return ConfigValue.IntValue(it) }
        s.toDoubleOrNull()?.let { return ConfigValue.DoubleValue(it) }

        return ConfigValue.StringValue(s)
    }

    private fun parseInlineList(raw: String, lineNo: Int): ConfigValue.ListValue {
        val inner = raw.trim().let {
            if (it.startsWith('[') && it.endsWith(']')) it.substring(1, it.length - 1)
            else throw ConfigParseException("Malformed inline list: $raw", lineNo)
        }
        if (inner.isBlank()) return ConfigValue.ListValue(emptyList())

        val items = splitTopLevel(inner, ',').map { parseScalar(it.trim()) }
        return ConfigValue.ListValue(items)
    }
    private fun parseInlineMap(raw: String, lineNo: Int): ConfigValue.MapValue {
        val inner = raw.trim().let {
            if (it.startsWith('[') && it.endsWith(']')) it.substring(1, it.length - 1)
            else throw ConfigParseException("Malformed inline map: $raw", lineNo)
        }
        if (inner.isBlank()) return ConfigValue.MapValue(emptyList())

        val entryRegex = Regex("""\{([^}]*)}""")
        val entries = entryRegex.findAll(inner).map { match ->
            val pairStr = match.groupValues[1]
            val parts = splitTopLevel(pairStr, ',')
            if (parts.size != 2)
                throw ConfigParseException("Map entry must have exactly 2 elements: {$pairStr}", lineNo)
            parseScalar(parts[0].trim()) to parseScalar(parts[1].trim())
        }.toList()

        return ConfigValue.MapValue(entries)
    }
    private fun splitTopLevel(input: String, delimiter: Char): List<String> {
        val result = mutableListOf<String>()
        val current = StringBuilder()
        var depth = 0
        var inString = false

        for (ch in input) {
            when {
                ch == '"' && depth == 0 -> { inString = !inString; current.append(ch) }
                inString -> current.append(ch)
                ch == '[' || ch == '{' -> { depth++; current.append(ch) }
                ch == ']' || ch == '}' -> { depth--; current.append(ch) }
                ch == delimiter && depth == 0 -> {
                    result.add(current.toString())
                    current.clear()
                }
                else -> current.append(ch)
            }
        }
        if (current.isNotBlank()) result.add(current.toString())
        return result
    }
    private fun String.stripInlineComment(): String {
        var inString = false
        for (i in indices) {
            val ch = this[i]
            if (ch == '"') inString = !inString
            if (!inString && ch == '#') return substring(0, i).trim()
        }
        return trim()
    }
}