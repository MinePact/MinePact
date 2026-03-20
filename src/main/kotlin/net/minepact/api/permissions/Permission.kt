package net.minepact.api.permissions

import java.time.Instant

data class Permission(
    val node: String,
    var value: Boolean = true,
    val expiresAt: Instant? = null
) {
    companion object {
        fun fromString(string: String): Permission {
            val parts = string
                .replace("[", "")
                .replace("]", "")
                .split(",")

            val node = parts[0]
            val value = parts[1].toBoolean()
            val expiresAt = parts.getOrNull(2)
                    ?.takeIf { it.isNotBlank() && it != "null" }
                    ?.let { Instant.parse(it) }

            return Permission(node, value, expiresAt)
        }
    }

    fun isTemporary(): Boolean = expiresAt != null
    fun toInfoString(): String {
        return "<white>${node} <gray>[${if (value) "true" else "false"}${expiresAt?.let { t -> ", expires at $t" } ?: ""}]"
    }
    override fun toString(): String = "[$node,$value,$expiresAt]"
}