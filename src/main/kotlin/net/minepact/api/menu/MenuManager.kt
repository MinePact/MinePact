package net.minepact.api.menu

import net.minepact.Main
import net.minepact.api.event.EventContext
import net.minepact.api.event.SimpleEventHandler
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import java.util.UUID

object MenuManager {
    private val openMenus: MutableMap<UUID, Menu> = mutableMapOf()

    fun registerOpenMenu(playerId: UUID, menu: Menu) {
        openMenus[playerId] = menu
    }
    fun unregisterOpenMenu(playerId: UUID) {
        openMenus.remove(playerId)
    }

    fun getOpenMenu(playerId: UUID): Menu? = openMenus[playerId]

    fun initialize() {
        try {
            val handler = object : SimpleEventHandler<InventoryClickEvent>() {
                override fun handle(context: EventContext<InventoryClickEvent>) {
                    val event = context.event
                    val player = event.whoClicked as? Player ?: return
                    val menu = getOpenMenu(player.uniqueId) ?: return

                    if (event.view.topInventory == menu.getInventory()) {
                        event.isCancelled = true
                        val slot = event.rawSlot
                        menu.handleClick(player, event, slot)
                    }
                }
            }
            Main.EVENT_REGISTRY.register(handler)
        } catch (ex: Throwable) {
            Main.instance.logger.warning("Failed to register MenuManager click handler: ${ex.message}")
        }
    }
}
