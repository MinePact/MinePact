package net.minepact.api.permissions.graph

class CompiledPermissionMap(
    val permissions: MutableMap<String, Boolean> = HashMap(128)
) {

    fun has(node: String): Boolean {
        permissions[node]?.let { return it }
        var check = node

        while (true) {
            val index = check.lastIndexOf('.')
            if (index == -1) break

            check = check.take(index)
            val wildcard = "$check.*"
            permissions[wildcard]?.let { return it }
        }

        return permissions["*"] ?: false
    }
}