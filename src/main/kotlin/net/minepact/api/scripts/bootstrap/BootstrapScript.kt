package net.minepact.api.scripts.bootstrap

import net.minepact.api.scripts.ScriptEntryDSL
import kotlin.script.experimental.annotations.KotlinScript

@KotlinScript(
    displayName = "MinePact Bootstrap",
    fileExtension = "bootstrap.minepact.kts",
    compilationConfiguration = BootstrapCompilationConfig::class,
    evaluationConfiguration = BootstrapEvalConfig::class
)
abstract class BootstrapScript(val bootstrap: BootstrapDSL) {
    fun loadOrder(vararg names: String) = bootstrap.loadOrder(*names)
    fun load(name: String, block: ScriptEntryDSL.() -> Unit = {}) = bootstrap.load(name, block)
    fun disableAutoDiscover() { bootstrap.autoDiscover = false }
}