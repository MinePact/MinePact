package net.minepact.api.item.enchantments

class EnchantmentInfo (
    val name: String,
    val colour: String,
    val maxLevel: Int,
    val maxPrestige: Int,
    val startPercentage: Double,
    val finalPercentage: Double,
    val applicable: List<Enchantable>,
    val trigger: EnchantmentTrigger,
    val triggerChanceType: TriggerChanceType,
    val exponentialScale: Int = 5
)