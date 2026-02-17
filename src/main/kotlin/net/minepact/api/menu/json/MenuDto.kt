package net.minepact.api.menu.json

data class MenuDto(
    val id: String,
    val title: String,
    val size: Int,
    val items: List<MenuItemDto>
)