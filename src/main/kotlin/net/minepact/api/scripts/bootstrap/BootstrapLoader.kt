package net.minepact.api.scripts.bootstrap

import java.io.File
import java.util.logging.Logger
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.api.constructorArgs
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost

class BootstrapLoader(private val logger: Logger) {
    private val host = BasicJvmScriptingHost()

    suspend fun load(file: File): BootstrapDSL? {
        logger.info("[ScriptEngine] Parsing bootstrap: ${file.name}")

        val dsl = BootstrapDSL()

        val compiled = when (val r = host.compiler(file.toScriptSource(), BootstrapCompilationConfig)) {
            is ResultWithDiagnostics.Success -> r.value
            is ResultWithDiagnostics.Failure -> {
                r.reports.forEach { logger.severe("[ScriptEngine] Bootstrap compile error: ${it.message}") }
                return null
            }
        }

        val evalConfig = ScriptEvaluationConfiguration(BootstrapEvalConfig) {
            constructorArgs(dsl)
        }

        return when (val r = host.evaluator(compiled, evalConfig)) {
            is ResultWithDiagnostics.Success -> dsl
            is ResultWithDiagnostics.Failure -> {
                r.reports.forEach { logger.severe("[ScriptEngine] Bootstrap eval error: ${it.message}") }
                null
            }
        }
    }
}