package net.minepact.api.data.sql

import net.minepact.api.data.DatabaseConfig

class MariaDBDatabase(config: DatabaseConfig) : SQLDatabase(config) {
    override fun createJdbcUrl(): String = "jdbc:mariadb://${config.host}:${config.port}/${config.database}"
    override fun driverClass(): String = "org.mariadb.jdbc.Driver"
}