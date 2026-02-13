package net.minepact.api.discord.embed

data class Field(
    val name: String,
    val value: String,
    val inline: Boolean = false
) {
    fun toJSON(): String = "{\"name\":\"${escape(name)}\",\"value\":\"${escape(value)}\",\"inline\":$inline}"
    private fun escape(s: String): String = s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
}