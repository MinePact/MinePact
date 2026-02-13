package net.minepact.core.global.events

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minepact.api.event.EventContext
import net.minepact.api.event.SimpleEventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType

class RenameMenuClickHandler : SimpleEventHandler<InventoryClickEvent>() {
    override fun handle(context: EventContext<InventoryClickEvent>) {
        val event = context.event

        val title = MiniMessage.miniMessage().deserialize("Rename Item")
        if (event.view.title() != title) return
        if (event.inventory.type != InventoryType.ANVIL) return

        event.isCancelled = true

        val player = event.whoClicked
        val item = player.inventory.itemInMainHand

        if (event.rawSlot == 2) {
            val newName: Component = event.view.topInventory.getItem(2)!!.itemMeta?.displayName() ?: return

            val meta = item.itemMeta
            meta.displayName(newName)
            item.itemMeta = meta

            player.closeInventory()
        }
    }
}