package net.minepact.api.permissions

import java.time.Duration

interface Permissable {
    fun getPermissions(): Set<Permission>
    fun hasPermission(permission: Permission): Boolean
    fun addPermission(permission: Permission)
    fun addTemporaryPermission(permission: Permission, duration: Duration)
    fun removePermission(permission: Permission)

    fun getGroups(): Set<Group>
    fun hasGroup(group: Group): Boolean
    fun addGroup(group: Group)
    fun removeGroup(group: Group)

    fun getAllPermissions(): Set<Permission> {
        val permissions = mutableSetOf<Permission>()
        permissions.addAll(getPermissions())
        getGroups().forEach { group ->
            permissions.addAll(group.permissions)
        }
        return permissions
    }
}