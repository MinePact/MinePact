package net.minepact.api.discord.embed

import java.net.URL

class Embed(
    val author: Author? = null,
    val title: String? = null,
    val url: URL? = null,
    val description: String? = null,
    val colour: Int? = null,
    val fields: List<Field> = emptyList(),
    val thumbnail: Image? = null,
    val image: Image? = null,
    val footer: Footer? = null
) {
    fun toJSON(): String {
        val parts = mutableListOf<String>()

        author?.let { parts += "\"author\": ${it.toJSON()}" }
        title?.let { parts += "\"title\": \"${escape(it)}\"" }
        url?.let { parts += "\"url\": \"${escape(it.toString())}\"" }
        description?.let { parts += "\"description\": \"${escape(it)}\"" }
        colour?.let { parts += "\"color\": $it" }
        val fieldsJson = fields.joinToString(separator = ",", prefix = "[", postfix = "]") { it.toJSON() }
        parts += "\"fields\": $fieldsJson"

        thumbnail?.let { if (it.url != null) parts += "\"thumbnail\": ${it.toJSON()}" }
        image?.let { if (it.url != null) parts += "\"image\": ${it.toJSON()}" }
        footer?.let { parts += "\"footer\": ${it.toJSON()}" }

        return "{${parts.joinToString(",")}}"
    }

    private fun escape(s: String): String = s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
}