package net.minepact.api.permissions

import net.minepact.api.data.repository.GroupRepository
import java.util.concurrent.CompletableFuture

object GroupRegistry {
    private val groups: MutableMap<String, Group> = mutableMapOf()

    fun register(group: Group) {
        groups[group.name.lowercase()] = group
    }
    fun unregister(group: Group) {
        groups.remove(group.name.lowercase())
    }

    fun get(name: String): CompletableFuture<Group?> {
        val lower = name.lowercase()
        groups[lower]?.let { return CompletableFuture.completedFuture(it) }

        return GroupRepository.findByName(name).thenApply { group ->
            if (group != null) register(group)
            group
        }
    }
    fun all(): Collection<Group> = groups.values
}