package net.minepact.api.config

interface FileWriter<T> {
    fun write(data: T)
}