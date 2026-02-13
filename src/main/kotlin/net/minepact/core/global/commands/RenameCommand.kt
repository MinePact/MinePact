package net.minepact.core.global.commands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minepact.api.command.Command
import net.minepact.api.command.CommandUsage
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Argument
import net.minepact.api.messages.send
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.AnvilInventory
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.view.AnvilView
import org.jetbrains.annotations.ApiStatus

class RenameCommand : Command(
    name = "rename",
    description = "Renames a player's current item.",
    usage = CommandUsage(label = "rename", arguments = listOf()),
    permission = "minepact.command.rename",
    aliases = mutableListOf(""),
    cooldown = 10.0,
    playerOnly = true
) {
    override fun execute(sender: CommandSender, args: MutableList<Argument<*>>): Result {
        val player = sender as Player
        val item = player.inventory.itemInMainHand

        if (item.isEmpty) {
            sender.send("<red>You must be holding an item to rename it.")
            return Result.FAILURE
        }

        val title = MiniMessage.miniMessage().deserialize("Rename Item")
        val anvil = Bukkit.createInventory(player, InventoryType.ANVIL, title)

        anvil.setItem(0, item.clone())
        player.openInventory(anvil)

        return Result.SUCCESS
    }
}