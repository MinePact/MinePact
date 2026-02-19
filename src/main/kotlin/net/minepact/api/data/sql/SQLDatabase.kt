package net.minepact.api.data.sql

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import net.minepact.api.data.Database
import net.minepact.api.data.DatabaseConfig
import java.sql.ResultSet
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

abstract class SQLDatabase(
    protected val config: DatabaseConfig
) : Database {
    private val executor = Executors.newFixedThreadPool(config.poolSize)
    protected val dataSource: HikariDataSource

    init {
        try {
            val hikari = HikariConfig().apply {
                jdbcUrl = createJdbcUrl()
                username = config.username
                password = config.password
                maximumPoolSize = config.poolSize
            }

            hikari.driverClassName = driverClass()
            dataSource = HikariDataSource(hikari)
        } catch(e: Exception) {
            throw RuntimeException("Failed to connect to the database!", e)
        }
    }

    protected abstract fun createJdbcUrl(): String
    protected abstract fun driverClass(): String

    override fun <T> query(
        sql: String,
        params: List<Any>,
        mapper: (ResultSet) -> T
    ): CompletableFuture<List<T>> {

        return CompletableFuture.supplyAsync({

            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->

                    params.forEachIndexed { index, param ->
                        stmt.setObject(index + 1, param)
                    }

                    val results = mutableListOf<T>()

                    stmt.executeQuery().use { rs ->
                        while (rs.next()) {
                            results.add(mapper(rs))
                        }
                    }

                    results
                }
            }

        }, executor)
    }

    override fun update(
        sql: String,
        params: List<Any>
    ): CompletableFuture<Int> {

        return CompletableFuture.supplyAsync({

            dataSource.connection.use { conn ->
                conn.prepareStatement(sql).use { stmt ->

                    params.forEachIndexed { index, param ->
                        stmt.setObject(index + 1, param)
                    }

                    stmt.executeUpdate()
                }
            }

        }, executor)
    }

    override fun shutdown() {
        dataSource.close()
        executor.shutdown()
    }
}