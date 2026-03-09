package net.minepact.api.config.yml

class YamlSection(
    identifier: String
) : YamlObject<MutableMap<String, YamlObject<*>>>(
    identifier,
    mutableMapOf()
) {
    override fun serialize(): String {
        return buildString {
            value.forEach { (_, obj) -> appendLine(obj.serialize()) }
        }
    }

    fun set(obj: YamlObject<*>) {
        value[obj.identifier.substringAfterLast(".")] = obj
    }
    fun get(key: String): YamlObject<*>? {
        return value[key]
    }
}