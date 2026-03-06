package net.minepact.api.scripts.bootstrap

import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.jvm.baseClassLoader
import kotlin.script.experimental.jvm.jvm

object BootstrapEvalConfig : ScriptEvaluationConfiguration({
    jvm {
        baseClassLoader(BootstrapScript::class.java.classLoader)
    }
}) {
    private fun readResolve(): Any = BootstrapEvalConfig
}