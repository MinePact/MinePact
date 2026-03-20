package net.minepact.api.command.dsl

import net.minepact.api.server.ServerType

abstract class Command(
    val server: ServerType = ServerType.GLOBAL
) {
    internal lateinit var root: CommandNode

    val name: String get() = root.name
    val description: String get() = root.description
    val aliases: List<String> get() = root.aliases

    protected fun command(name: String, block: CommandBuilder.() -> Unit) {
        root = CommandBuilder(name).apply(block).build()
    }

    fun buildUsage(args: Array<String> = emptyArray()): String {
        val (node, prefixSegments) = walkToNode(root, args, 0, listOf("/${root.name}"))
        val paths = collectLeafPaths(node).filter { it.isNotEmpty() }
        val prefix = prefixSegments.joinToString(" ")
        if (paths.isEmpty()) return prefix
        return "$prefix ${mergePaths(paths)}"
    }

    private fun walkToNode(
        node: CommandNode,
        args: Array<String>,
        index: Int,
        segments: List<String>
    ): Pair<CommandNode, List<String>> {
        if (index >= args.size) return node to segments
        val input = args[index]

        // Literal match takes priority
        node.children
            .firstOrNull { it.type == CommandNode.Type.LITERAL && it.matches(input) }
            ?.let { return walkToNode(it, args, index + 1, segments + it.segment()) }

        // Fall through to argument node
        node.children
            .firstOrNull { it.type == CommandNode.Type.ARGUMENT }
            ?.let { return walkToNode(it, args, index + 1, segments + it.segment()) }

        return node to segments
    }
    private fun collectLeafPaths(node: CommandNode): List<List<String>> {
        val result = mutableListOf<List<String>>()
        if (node.executor != null || node.children.isEmpty()) result.add(emptyList())
        node.children.forEach { child ->
            collectLeafPaths(child).mapTo(result) { listOf(child.segment()) + it }
        }
        return result
    }
    private fun CommandNode.segment(): String = when (type) {
        CommandNode.Type.LITERAL -> name
        CommandNode.Type.ARGUMENT -> if (argument!!.optional) "[${name}]" else "<${name}>"
    }
    private fun mergePaths(paths: List<List<String>>): String {
        if (paths.isEmpty()) return ""
        if (paths.size == 1) return paths[0].joinToString(" ")
        if (paths.all { it.isEmpty() }) return ""

        val maxPrefix = paths.minOf { it.size }
        var prefixLen = 0
        while (prefixLen < maxPrefix && paths.all { it[prefixLen] == paths[0][prefixLen] }) prefixLen++

        val prefix = paths[0].take(prefixLen).joinToString(" ")
        val tails = paths.map { it.drop(prefixLen) }

        if (tails.all { it.isEmpty() }) return prefix

        val groups = tails.groupBy { it.firstOrNull() }
        val groupSuffixes = groups.mapValues { (_, v) -> mergePaths(v.map { it.drop(1) }) }
        val uniqueSuffixes = groupSuffixes.values.distinct()

        val diverged = if (uniqueSuffixes.size == 1 && groups.none { it.key == null }) {
            val keys = groups.keys.filterNotNull()
            val grouped = if (keys.size > 1) "<${keys.joinToString(" | ")}>" else keys[0]
            val suffix = uniqueSuffixes[0]
            if (suffix.isNotEmpty()) "$grouped $suffix" else grouped
        } else {
            groups.entries.joinToString("\n") { (key, v) ->
                val keyStr = key ?: ""
                val suf = mergePaths(v.map { it.drop(1) })
                listOf(keyStr, suf).filter { it.isNotEmpty() }.joinToString(" ")
            }
        }

        return listOf(prefix, diverged).filter { it.isNotEmpty() }.joinToString(" ")
    }

    override fun toString() = "Command[name=${root.name}, description=${root.description}]"
    override fun equals(other: Any?) = other is Command && name == other.name && javaClass.simpleName == other.javaClass.simpleName
    override fun hashCode() = 31 * name.hashCode() + javaClass.simpleName.hashCode()
}