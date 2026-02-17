package net.minepact.api.data

import net.minepact.api.data.sql.MariaDBDatabase
import net.minepact.api.data.sql.MySQLDatabase
import net.minepact.api.data.sql.SQLiteDatabase

object DatabaseFactory {
    fun create(config: DatabaseConfig): Database {
        return when (config.type) {
            DatabaseProvider.MYSQL -> MySQLDatabase(config)
            DatabaseProvider.MARIADB -> MariaDBDatabase(config)
            DatabaseProvider.SQLITE -> SQLiteDatabase(config)
        }
    }
}