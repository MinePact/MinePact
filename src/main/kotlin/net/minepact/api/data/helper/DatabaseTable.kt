package net.minepact.api.data.helper

class DatabaseTable(
    val name: String,
    val columns: List<DatabaseColumn>
) {
    fun createEnsureScript(): String {
        val columnDefinitions = columns.joinToString(", ") { it.toString() }
        return "CREATE TABLE IF NOT EXISTS $name ($columnDefinitions);"
    }
}