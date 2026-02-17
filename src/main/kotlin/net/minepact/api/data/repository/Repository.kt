package net.minepact.api.data.repository

import net.minepact.Main
import net.minepact.api.data.Database
import java.sql.ResultSet
import java.util.concurrent.CompletableFuture

abstract class Repository<T> {
    protected var database: Database = Main.DATABASE

    init {
        ensureTableExists()
    }

    abstract fun table(): String
    abstract fun map(rs: ResultSet): T

    abstract fun insertColumns(): List<String>
    abstract fun insertValues(entity: T): List<Any>

    abstract fun ensureTableExists()

    fun findAll(): CompletableFuture<List<T>> {
        return database.query(
            "SELECT * FROM ${table()}",
            mapper = ::map
        )
    }
    fun insert(entity: T): CompletableFuture<Int> {
        val columns = insertColumns()
        val placeholders = columns.joinToString(", ") { "?" }
        val updateClause = columns.joinToString(", ") { "$it = VALUES($it)" }

        val sql = """
        INSERT INTO ${table()} (${columns.joinToString(", ")})
        VALUES ($placeholders)
        ON DUPLICATE KEY UPDATE $updateClause
    """.trimIndent()

        return database.update(sql, insertValues(entity))
    }

    fun deleteById(id: Any): CompletableFuture<Int> {
        return database.update(
            "DELETE FROM ${table()} WHERE id = ?",
            listOf(id)
        )
    }
}