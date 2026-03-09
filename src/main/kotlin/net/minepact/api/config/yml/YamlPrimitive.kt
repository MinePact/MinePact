package net.minepact.api.config.yml

class YamlPrimitive<T>(
    identifier: String,
    value: T
) : YamlObject<T>(identifier, value) {
    override fun serialize(): String {
        return value.toString()
    }
}