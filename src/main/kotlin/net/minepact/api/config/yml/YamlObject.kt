package net.minepact.api.config.yml

abstract class YamlObject<T>(
    val identifier: String,
    var value: T
) {
    abstract fun serialize(): String
}