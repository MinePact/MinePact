package net.minepact.api.data.helper

class DatabaseColumn(
    val name: String,
    val type: DataType,
    val isPrimaryKey: Boolean = false,
    val isAutoIncrement: Boolean = false,
    val isNullable: Boolean = false,
    val defaultValue: Any? = null
) {
    override fun toString(): String {
        val constraints = mutableListOf<String>()
        // PRIMARY KEY is intentionally handled at the table level to allow composite keys
        if (isAutoIncrement) constraints.add("AUTO_INCREMENT")
        if (!isNullable) constraints.add("NOT NULL")
        if (defaultValue != null) constraints.add("DEFAULT $defaultValue")

        return "$name ${type.rep} ${constraints.joinToString(" ")}".trim()
    }
}
