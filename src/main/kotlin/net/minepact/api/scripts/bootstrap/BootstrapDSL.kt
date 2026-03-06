package net.minepact.api.scripts.bootstrap

import net.minepact.api.scripts.ScriptEntryDSL

class BootstrapDSL {
    val order = mutableListOf<String>()
    val explicitDeps = mutableMapOf<String, Set<String>>()
    var autoDiscover = true

    fun loadOrder(vararg names: String) { order.addAll(names) }
    fun load(name: String, block: ScriptEntryDSL.() -> Unit = {}) {
        order.add(name)
        val entry = ScriptEntryDSL().also(block)
        if (entry.deps.isNotEmpty()) explicitDeps[name] = entry.deps.toSet()
    }
}