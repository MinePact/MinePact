package net.minepact.api.data

import java.sql.ResultSet
import java.util.concurrent.CompletableFuture

interface Database {
    fun <T> query(
        sql: String,
        params: List<Any> = emptyList(),
        mapper: (ResultSet) -> T
    ): CompletableFuture<List<T>>
    fun update(
        sql: String,
        params: List<Any> = emptyList()
    ): CompletableFuture<Int>
    fun shutdown()
}