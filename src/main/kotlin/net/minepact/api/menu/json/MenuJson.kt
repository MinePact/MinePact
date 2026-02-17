package net.minepact.api.menu.json

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minepact.api.menu.Menu

object MenuJson {
    private val gson: Gson = GsonBuilder().create()
    private val mm = MiniMessage.miniMessage()

    fun serialize(menu: Menu): String {
        val dto = MenuDto(
            id = menu.id,
            title = mm.serialize(menu.title),
            size = menu.getInventory().size,
            items = menu.getInventory().contents.mapIndexedNotNull { idx, stack ->
                if (stack == null) return@mapIndexedNotNull null
                MenuItemDto(idx, stack.type.name)
            }
        )
        return gson.toJson(dto)
    }
    fun deserialize(json: String): MenuDto {
        return gson.fromJson(json, MenuDto::class.java)
    }
}

