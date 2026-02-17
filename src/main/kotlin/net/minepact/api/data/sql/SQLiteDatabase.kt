package net.minepact.api.data.sql

import net.minepact.api.data.DatabaseConfig

class SQLiteDatabase(config: DatabaseConfig) : SQLDatabase(config) {
    override fun createJdbcUrl(): String = "jdbc:sqlite:${config.filePath}"
    override fun driverClass(): String = "org.sqlite.JDBC"
}