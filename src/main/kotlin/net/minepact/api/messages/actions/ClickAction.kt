package net.minepact.api.messages.actions

sealed interface ClickAction {
    val value: String

    data class OpenUrl(override val value: String) : ClickAction
    data class RunCommand(override val value: String) : ClickAction
    data class SuggestCommand(override val value: String) : ClickAction
}