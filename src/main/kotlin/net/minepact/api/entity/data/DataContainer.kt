package net.minepact.api.entity.data

class DataContainer {
    private val data = mutableMapOf<DataKey<*>, Any>()

    fun <T> set(key: DataKey<T>, value: T) {
        data[key] = value as Any
    }
    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: DataKey<T>): T? {
        return data[key] as? T
    }

    fun has(key: DataKey<*>): Boolean {
        return data.containsKey(key)
    }
    fun remove(key: DataKey<*>) {
        data.remove(key)
    }
}