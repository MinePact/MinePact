package net.minepact.api.permissions

import java.time.Instant

data class Group(
    val name: String,
    var displayName: String?,
    var weight: Int = 0,
    var prefix: String? = null,
    var suffix: String? = null,
    val permissions: MutableSet<Permission> = mutableSetOf(),
    val parents: MutableSet<String> = mutableSetOf(),
    val expiresAt: Instant? = null
) {
    companion object {
        fun fromString(string: String): Group {
            val parts = string.split(",")
            val name = parts[0]
            val displayName = parts[1].takeIf { it != "null" && !it.isEmpty() }
            val weight = parts[2].toInt()
            val prefix = parts[3].takeIf { it != "null" && !it.isEmpty() }
            val suffix = parts[4].takeIf { it != "null" && !it.isEmpty() }
            val permissions = if (parts[5] == "null" || parts[5] == "") mutableSetOf() else parts[5].split("~").map { Permission.fromString(it) }.toMutableSet()
            val expiresAt = parts[6].takeIf { it != "null"  && !it.isEmpty() }?.let { Instant.parse(it) }

            return Group(
                name = name,
                displayName = displayName,
                weight = weight,
                prefix = prefix,
                suffix = suffix,
                permissions = permissions,
                expiresAt = expiresAt
            )
        }
    }

    fun isTemporary(): Boolean = expiresAt != null
    fun isExpired(): Boolean = expiresAt != null && Instant.now().isAfter(expiresAt)
    override fun toString(): String = "[$name,$displayName,$weight,$prefix,$suffix,${permissions.joinToString("~")},$expiresAt]"
}