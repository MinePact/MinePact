package net.minepact.api.config.custom.exception

class ConfigListTypeMismatchException(
    val path: String,
    val expected: String,
    val actual: String,
    val index: Int = -1
) : ConfigException(
    buildString {
        append("Type mismatch in list at '$path'")
        if (index >= 0) append(" (index $index)")
        append(": expected $expected, found $actual")
    }
)