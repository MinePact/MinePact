package net.minepact.api.config.yml

class YamlList<T>(
    identifier: String,
    value: MutableList<T>
) : YamlObject<MutableList<T>>(identifier, value) {
    override fun serialize(): String {
        return buildString {
            value.forEach { appendLine("- $it") }
        }
    }
}