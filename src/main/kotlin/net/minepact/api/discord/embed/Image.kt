package net.minepact.api.discord.embed

import java.net.URL

data class Image(
    val url: URL?
) {
    fun toJSON(): String {
        if (url == null) return "{}"
        return "{\"url\":\"${escape(url.toString())}\"}"
    }

    private fun escape(s: String): String = s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
}