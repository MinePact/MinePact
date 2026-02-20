package net.minepact.api.data.repository

import net.minepact.Main
import net.minepact.api.data.Database
import net.minepact.api.data.helper.DatabaseTable
import java.sql.ResultSet
import java.util.concurrent.CompletableFuture

abstract class Repository<T> {
    protected var database: Database = Main.DATABASE

    init {
        ensureTableExists()
    }

    abstract fun table(): DatabaseTable
    fun tableName(): String = table().name
    abstract fun map(rs: ResultSet): T

    fun insertColumns(): List<String> = table().columns.map { it.name }
    abstract fun insertValues(entity: T): List<Any>

    private fun ensureTableExists() {
        val sql = table().createEnsureScript()
        database.update(sql).thenAccept {
            Main.instance.logger.info {
                "[${this.javaClass.simpleName}] Table created if not found."
            }
        }
    }

    fun findAll(): CompletableFuture<List<T>> {
        return database.query(
            "SELECT * FROM ${tableName()}",
            mapper = ::map
        )
    }
    fun insert(entity: T): CompletableFuture<Int> {
        val columns = insertColumns()
        val placeholders = columns.joinToString(", ") { "?" }
        val updateClause = columns.joinToString(", ") { "$it = VALUES($it)" }

        val sql = """
        INSERT INTO ${tableName()} (${columns.joinToString(", ")})
        VALUES ($placeholders)
        ON DUPLICATE KEY UPDATE $updateClause
    """.trimIndent()

        return database.update(sql, insertValues(entity))
    }

    fun deleteById(id: Any): CompletableFuture<Int> {
        return database.update(
            "DELETE FROM ${tableName()} WHERE id = ?",
            listOf(id)
        )
    }

    protected fun <T> querySingle(
        sql: String,
        params: List<Any> = emptyList(),
        mapper: (ResultSet) -> T
    ): CompletableFuture<T?> {
        return database.query(sql, params, mapper)
            .thenApply { results -> results.firstOrNull() }
    }
    protected fun <T> queryList(
        sql: String,
        params: List<Any> = emptyList(),
        mapper: (ResultSet) -> T
    ): CompletableFuture<List<T>> = database.query(sql, params, mapper)
}