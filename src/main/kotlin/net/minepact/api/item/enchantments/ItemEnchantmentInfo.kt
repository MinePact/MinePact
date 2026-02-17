package net.minepact.api.item.enchantments

import net.minepact.Main
import org.bukkit.NamespacedKey

data class ItemEnchantmentInfo(
    val info: EnchantmentInfo,
    val currentLevel: Int,
    val currentPrestige: Int
) {
    companion object {
        fun parseEnchantKey(keyString: String): ItemEnchantmentInfo? {
            val pattern = Regex("""enchant_([^/]+)/([^/]+)/(\d+)/(\d+)/([\d.]+)/([\d.]+)/([^/]+)/([^/]+)/([^;]+)--(\d+)/(\d+)""")
            val match = pattern.matchEntire(keyString) ?: return null

            return ItemEnchantmentInfo(
                info = EnchantmentInfo(
                    name = match.groupValues[1],
                    colour = match.groupValues[2],
                    maxLevel = match.groupValues[3].toInt(),
                    maxPrestige = match.groupValues[4].toInt(),
                    startPercentage = match.groupValues[5].toDouble(),
                    finalPercentage = match.groupValues[6].toDouble(),
                    applicable = Enchantable.fromString(match.groupValues[7]),
                    trigger = EnchantmentTrigger.valueOf(match.groupValues[8].uppercase()),
                    triggerChanceType = TriggerChanceType.valueOf(match.groupValues[9].uppercase())
                ),
                currentLevel = match.groupValues[10].toInt(),
                currentPrestige = match.groupValues[11].toInt()
            )
        }
    }
    fun parseEnchantKey(keyString: String): ItemEnchantmentInfo? {
        val pattern = Regex("""enchant_([^/]+)/([^/]+)/(\d+)/(\d+)/([\d.]+)/([\d.]+)/([^/]+)/([^/]+)/([^;]+)--(\d+)/(\d+)""")
        val match = pattern.matchEntire(keyString) ?: return null

        return ItemEnchantmentInfo(
            info = EnchantmentInfo(
                name = match.groupValues[1],
                colour = match.groupValues[2],
                maxLevel = match.groupValues[3].toInt(),
                maxPrestige = match.groupValues[4].toInt(),
                startPercentage = match.groupValues[5].toDouble(),
                finalPercentage = match.groupValues[6].toDouble(),
                applicable = Enchantable.fromString(match.groupValues[7]),
                trigger = EnchantmentTrigger.valueOf(match.groupValues[8].uppercase()),
                triggerChanceType = TriggerChanceType.valueOf(match.groupValues[9].uppercase())
            ),
            currentLevel = match.groupValues[10].toInt(),
            currentPrestige = match.groupValues[11].toInt()
        )
    }

    fun getEnchantmentKeyString(): String {
        val applicableString = info.applicable.joinToString(",") { it.toString() }
        return "enchant_${info.name}/${info.colour}/${info.maxLevel}/${info.maxPrestige}/${info.startPercentage}/${info.finalPercentage}/${applicableString}/${info.trigger.name}/${info.triggerChanceType.name}--$currentLevel/$currentPrestige"
    }
    fun getEnchantmentKey(): NamespacedKey {
        return NamespacedKey(Main.instance, getEnchantmentKeyString().lowercase())
    }
}