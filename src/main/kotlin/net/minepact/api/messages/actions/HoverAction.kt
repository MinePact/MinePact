package net.minepact.api.messages.actions

sealed interface HoverAction {
    data class ShowText(val text: String) : HoverAction
}