package net.minepact.api.discord.embed

import java.net.URL

data class Author(
    val name: String? = null,
    val url: URL? = null,
    val iconUrl: URL? = null
) {
    fun toJSON(): String {
        val parts = mutableListOf<String>()
        name?.let { parts += "\"name\":\"${escape(it)}\"" }
        url?.let { parts += "\"url\":\"${escape(it.toString())}\"" }
        iconUrl?.let { parts += "\"icon_url\":\"${escape(it.toString())}\"" }
        return "{${parts.joinToString(",")}}"
    }
    private fun escape(s: String): String = s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
}