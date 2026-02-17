package net.minepact.core.global.menues

import net.minepact.api.item.Item
import net.minepact.api.menu.Menu
import net.minepact.api.menu.MenuBuilder
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.entity.Player

class ExampleMenu(
    private val title: String = "<red><b>Example Menu",
    private val rows: Int = 5
) {
    private val mm = MiniMessage.miniMessage()

    fun build(): Menu {
        val titleComponent = mm.deserialize(title)

        val stringItem = Item(Material.STRING)
        val diamondItem = Item(Material.DIAMOND)

        return MenuBuilder("example_menu")
            .title(titleComponent)
            .size(rows * 9)
            .item(0, stringItem) {
                displayName(mm.deserialize("<yellow>String"))
                onClick {
                    player.sendMessage("You clicked the string!")
                    close()
                }
            }
            .item(13, diamondItem) {
                displayName(mm.deserialize("<aqua>Diamond"))
                onClick {
                    player.sendMessage("You clicked the diamond!")
                }
            }
            .build()
    }

    fun open(player: Player) {
        build().open(player)
    }
}