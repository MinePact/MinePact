package net.minepact.api.permissions

import net.minepact.api.permissions.graph.CompiledPermissionMap
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object PermissionCache {
    private val cache = ConcurrentHashMap<UUID, CompiledPermissionMap>()

    fun get(uuid: UUID): CompiledPermissionMap? = cache[uuid]
    fun put(uuid: UUID, map: CompiledPermissionMap) {
        cache[uuid] = map
    }

    fun invalidate(uuid: UUID) {

        cache.remove(uuid)
    }
}