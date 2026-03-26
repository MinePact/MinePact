package net.minepact.api.permissions.repository

import net.minepact.api.constants.GROUP_SEPARATOR
import net.minepact.api.permissions.Group

data class PlayerGroupData(
    val groups: MutableList<Group>,
) {
    override fun toString(): String = groups.joinToString(separator = "$GROUP_SEPARATOR")
}