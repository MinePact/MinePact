package net.minepact.api.item.enchantments

interface Enchantable {
    companion object {
        private val allEntries: List<Enchantable> = EnchantmentType.entries + EnchantmentType.Single.entries

        fun parse(name: String): Enchantable {
            return allEntries.first { (it as Enum<*>).name.equals(name, ignoreCase = true) }
        }
        fun fromString(name: String): List<Enchantable> {
            return name
                .replace("[", "", true)
                .replace("]", "", true)
                .split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .map { parse(it) }
        }
        fun fromStringList(s: String): List<Enchantable> {
            return s.replace("[", "")
                .replace("]", "")
                .split(",")
                .mapNotNull { name ->
                    try {
                        EnchantmentType.entries.firstOrNull {
                            it.name.equals(name.trim(), true)
                        } ?: EnchantmentType.Single.entries.firstOrNull {
                            it.name.equals(name.trim(), true)
                        }
                    } catch (_: Exception) { null }
                }
        }
    }
}