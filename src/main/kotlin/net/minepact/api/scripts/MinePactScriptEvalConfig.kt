package net.minepact.api.scripts

import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.jvm.baseClassLoader
import kotlin.script.experimental.jvm.jvm

object MinePactScriptEvalConfig : ScriptEvaluationConfiguration({
    jvm {
        baseClassLoader(MinePactScript::class.java.classLoader)
    }
}) {
    private fun readResolve(): Any = MinePactScriptEvalConfig
}