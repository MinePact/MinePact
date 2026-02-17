package net.minepact.api.item.enchantments

import org.bukkit.Material
import org.bukkit.Material.*

enum class EnchantmentType(
    val items: List<Single>
) : Enchantable {
    ARMOUR(listOf(Single.HELMET, Single.CHESTPLATE, Single.LEGGINGS, Single.BOOTS, Single.ELYTRA)),
    TOOLS(listOf(Single.SWORD, Single.PICKAXE, Single.AXE, Single.SHOVEL, Single.HOE));

    enum class Single(
        val items: List<Material>
    ) : Enchantable {
        HELMET(listOf(
            LEATHER_HELMET,
            CHAINMAIL_HELMET,
            IRON_HELMET,
            GOLDEN_HELMET,
            DIAMOND_HELMET,
            NETHERITE_HELMET,
            TURTLE_HELMET
        )),
        CHESTPLATE(listOf(
            LEATHER_CHESTPLATE,
            CHAINMAIL_CHESTPLATE,
            IRON_CHESTPLATE,
            GOLDEN_CHESTPLATE,
            DIAMOND_CHESTPLATE,
            NETHERITE_CHESTPLATE
        )),
        LEGGINGS(listOf(
            LEATHER_LEGGINGS,
            CHAINMAIL_LEGGINGS,
            IRON_LEGGINGS,
            GOLDEN_LEGGINGS,
            DIAMOND_LEGGINGS,
            NETHERITE_LEGGINGS
        )),
        BOOTS(listOf(
            LEATHER_BOOTS,
            CHAINMAIL_BOOTS,
            IRON_BOOTS,
            GOLDEN_BOOTS,
            DIAMOND_BOOTS,
            NETHERITE_BOOTS
        )),
        SWORD(listOf(
            WOODEN_SWORD,
            STONE_SWORD,
            IRON_SWORD,
            GOLDEN_SWORD,
            DIAMOND_SWORD,
            NETHERITE_SWORD
        )),
        PICKAXE(listOf(
            WOODEN_PICKAXE,
            STONE_PICKAXE,
            IRON_PICKAXE,
            GOLDEN_PICKAXE,
            DIAMOND_PICKAXE,
            NETHERITE_PICKAXE
        )),
        AXE(listOf(
            WOODEN_AXE,
            STONE_AXE,
            IRON_AXE,
            GOLDEN_AXE,
            DIAMOND_AXE,
            NETHERITE_AXE
        )),
        SHOVEL(listOf(
            WOODEN_SHOVEL,
            STONE_SHOVEL,
            IRON_SHOVEL,
            GOLDEN_SHOVEL,
            DIAMOND_SHOVEL,
            NETHERITE_SHOVEL
        )),
        HOE(listOf(
            WOODEN_HOE,
            STONE_HOE,
            IRON_HOE,
            GOLDEN_HOE,
            DIAMOND_HOE,
            NETHERITE_HOE
        )),
        ELYTRA(listOf(
            Material.ELYTRA
        ));
    }
}