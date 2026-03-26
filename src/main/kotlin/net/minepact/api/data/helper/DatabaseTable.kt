package net.minepact.api.data.helper

class DatabaseTable(
    val name: String,
    val columns: List<DatabaseColumn>
) {
    fun createEnsureScript(): String {
        val columnDefinitions = columns.joinToString(", ") { it.toString() }
        val pkColumns = columns.filter { it.isPrimaryKey }.map { it.name }
        val pkClause = if (pkColumns.isNotEmpty()) ", PRIMARY KEY(${pkColumns.joinToString(", ")})" else ""
        return "CREATE TABLE IF NOT EXISTS $name ($columnDefinitions$pkClause);"
    }
}