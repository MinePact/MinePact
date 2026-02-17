package net.minepact.api.data

data class DatabaseConfig(
    val type: DatabaseProvider,
    val host: String = "localhost",
    val port: Int = 3306,
    val database: String,
    val username: String = "",
    val password: String = "",
    val filePath: String = "database.db", // Dev note -> Only used for sqlite
    val poolSize: Int = 10
)