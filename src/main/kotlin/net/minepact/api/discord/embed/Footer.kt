package net.minepact.api.discord.embed

import java.net.URL

data class Footer(
    val text: String? = null,
    val iconUrl: URL? = null
) {
    fun toJSON(): String {
        val parts = mutableListOf<String>()
        text?.let { parts += "\"text\":\"${escape(it)}\"" }
        iconUrl?.let { parts += "\"icon_url\":\"${escape(it.toString())}\"" }
        return "{${parts.joinToString(",")}}"
    }
    private fun escape(s: String): String = s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
}