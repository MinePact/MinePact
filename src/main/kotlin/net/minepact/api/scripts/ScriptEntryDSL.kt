package net.minepact.api.scripts

class ScriptEntryDSL {
    internal val deps = mutableListOf<String>()
    fun dependsOn(vararg scripts: String) { deps.addAll(scripts) }
}