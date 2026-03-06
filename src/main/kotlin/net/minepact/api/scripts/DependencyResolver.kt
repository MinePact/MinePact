package net.minepact.api.scripts

class DependencyResolver {
    fun resolve(scripts: Map<String, Set<String>>): List<String> {
        validateAllDepsExist(scripts)
        return topologicalSort(scripts)
    }

    private fun validateAllDepsExist(scripts: Map<String, Set<String>>) {
        for ((name, deps) in scripts) {
            for (dep in deps) {
                if (dep in scripts) continue

                throw IllegalStateException(
                        "Script '$name' declares @DependsOn(\"$dep\") but no script named '$dep' was found.\n" +
                        "Available scripts: [${scripts.keys.sorted().joinToString()}]"
                )
            }
        }
    }
    private fun topologicalSort(scripts: Map<String, Set<String>>): List<String> {
        val inDegree = scripts.keys.associateWithTo(mutableMapOf()) { 0 }
        val dependents = scripts.keys.associateWithTo(mutableMapOf()) { mutableListOf<String>() }

        for ((script, deps) in scripts) {
            for (dep in deps) {
                dependents[dep]!!.add(script)
                inDegree[script] = inDegree[script]!! + 1
            }
        }

        val queue = ArrayDeque(inDegree.filterValues { it == 0 }.keys.sorted())
        val sorted = mutableListOf<String>()

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            sorted.add(current)
            dependents[current]!!
                .onEach { inDegree[it] = inDegree[it]!! - 1 }
                .filter { inDegree[it] == 0 }
                .sortedBy { it }
                .forEach { queue.add(it) }
        }

        if (sorted.size != scripts.size) {
            val cycle = (scripts.keys - sorted.toSet()).sorted()
            throw IllegalStateException(
                    "Circular dependency detected! Scripts in cycle: [${cycle.joinToString()}]\n" +
                    "Check the @DependsOn annotations (or bootstrap.minepact.kts) for these scripts."
            )
        }

        return sorted
    }
}