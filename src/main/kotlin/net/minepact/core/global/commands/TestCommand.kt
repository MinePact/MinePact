package net.minepact.core.global.commands

import net.minepact.api.command.Command
import net.minepact.api.command.CommandUsage
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Argument
import net.minepact.api.item.Item
import net.minepact.api.messages.FormatParser
import net.minepact.api.player.permissions.Permission
import org.bukkit.Material
import org.bukkit.entity.Player

class TestCommand : Command(
    name = "test",
    description = "A testing command for the developers.",
    permission = Permission("minepact.dev.test"),
    aliases = mutableListOf(""),
    usage = CommandUsage(
        label = "test",
        arguments = listOf()
    ),
    cooldown = 1.0,
    playerOnly = true
) {
    override fun execute(
        sender: net.minepact.api.player.Player,
        args: MutableList<Argument<*>>
    ): Result {
        val item: Item = Item(
            Material.DIAMOND_PICKAXE,
            name = FormatParser.parse("<from:3F5EFB><bold>Test Enchant Item</bold><to:FC466B>"),
            lore = listOf(
                FormatParser.parse("<from:3D58DB>| This is a test item for enchantments!<to:BD4FD6>"),
                FormatParser.parse("<from:3D58DB>| It has a custom name and lore!<to:BD4FD6>")
            ),
            enchantable = true
        )

        (sender as Player).inventory.addItem(item.toItemStack())
        return Result.SUCCESS
    }
}