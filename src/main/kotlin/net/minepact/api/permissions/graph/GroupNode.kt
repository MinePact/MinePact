package net.minepact.api.permissions.graph

import net.minepact.api.permissions.Group
import net.minepact.api.permissions.Permission

class GroupNode(val group: Group) {
    val parents: MutableSet<GroupNode> = mutableSetOf()
    val permissions: MutableSet<Permission>
        get() = group.permissions
}