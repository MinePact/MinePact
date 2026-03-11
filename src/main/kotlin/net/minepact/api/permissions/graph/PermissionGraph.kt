package net.minepact.api.permissions.graph

import net.minepact.api.permissions.Group
import net.minepact.api.permissions.GroupRegistry

object PermissionGraph {
    private val nodes: MutableMap<String, GroupNode> = mutableMapOf()

    fun build() {
        nodes.clear()
        GroupRegistry.all().forEach { group -> nodes[group.name] = GroupNode(group) }

        nodes.values.forEach { node ->
            node.group.parents.forEach { parentName ->
                val parent = nodes[parentName] ?: return@forEach
                node.parents.add(parent)
            }
        }
    }

    fun getNode(group: Group): GroupNode? = nodes[group.name]
}