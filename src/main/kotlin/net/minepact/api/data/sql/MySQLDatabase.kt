package net.minepact.api.data.sql

import net.minepact.api.data.DatabaseConfig

class MySQLDatabase(config: DatabaseConfig) : SQLDatabase(config) {
    override fun createJdbcUrl(): String = "jdbc:mysql://${config.host}:${config.port}/${config.database}?useSSL=false&autoReconnect=true"
    override fun driverClass(): String = "com.mysql.cj.jdbc.Driver"
}