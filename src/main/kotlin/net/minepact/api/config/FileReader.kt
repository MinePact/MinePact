package net.minepact.api.config

interface FileReader<T> {
    fun read(): T
}