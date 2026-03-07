package net.minepact.api.config.custom.interfaces

interface FileWriter {
    fun set(path: String, value: String)
    fun set(path: String, value: Int)
    fun set(path: String, value: Double)
    fun set(path: String, value: Float)
    fun set(path: String, value: Boolean)

    fun <T> setList(path: String, values: List<T>)
    fun <K, V> setMap(path: String, entries: Map<K, V>)

    fun remove(path: String)
    fun contains(path: String): Boolean

    fun save()
    fun reload()
}