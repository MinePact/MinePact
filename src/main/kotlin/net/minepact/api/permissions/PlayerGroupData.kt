package net.minepact.api.permissions

import net.minepact.api.constants.GROUP_SEPARATOR

data class PlayerGroupData(
    val groups: MutableList<Group>,
) {
    override fun toString(): String = groups.joinToString(separator = "$GROUP_SEPARATOR")
}