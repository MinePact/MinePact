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

            val expiresAt =
                parts.getOrNull(2)
                    ?.takeIf { it.isNotBlank() && it != "null" }
                    ?.let { Instant.parse(it) }

            return Permission(node, value, expiresAt)
        }
    }

    fun isTemporary(): Boolean = expiresAt != null
    override fun toString(): String = "[$node,$value,$expiresAt]"
}