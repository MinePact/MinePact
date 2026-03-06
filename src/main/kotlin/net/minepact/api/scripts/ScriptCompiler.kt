package net.minepact.api.scripts

import net.minepact.api.scripts.exceptions.ScriptEvaluationException
import java.io.File
import java.security.MessageDigest
import java.util.logging.Logger
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvmhost.*

class ScriptCompiler(
    private val logger: Logger,
    cacheDir: File
) {
    private val host = BasicJvmScriptingHost()
    private val cache = mutableMapOf<String, CompiledScript>()

    init {
        cacheDir.mkdirs()
    }

    suspend fun compile(file: File): Result<CompiledScript> {
        val hash = file.sha256()
        cache[hash]?.let { return Result.success(it) }

        logger.fine("[ScriptEngine] Compiling ${file.name}...")
        val start = System.currentTimeMillis()

        val result = host.compiler(file.toScriptSource(), MinePactScriptCompilationConfig)
        val elapsed = System.currentTimeMillis() - start

        return when (result) {
            is ResultWithDiagnostics.Success -> {
                logger.fine("[ScriptEngine] Compiled ${file.name} in ${elapsed}ms")
                cache[hash] = result.value
                Result.success(result.value)
            }
            is ResultWithDiagnostics.Failure -> {
                val errors = result.reports
                    .filter { it.severity >= ScriptDiagnostic.Severity.WARNING }
                    .joinToString("\n") { report ->
                        val loc = report.location?.let { " (line ${it.start.line})" } ?: ""
                        "  [${report.severity}]$loc ${report.message}"
                    }
                Result.failure(net.minepact.api.scripts.exceptions.ScriptCompilationException(file.name, errors))
            }
        }
    }
    suspend fun evaluate(compiled: CompiledScript, api: ScriptAPI): Result<Any?> {
        val evalConfig = ScriptEvaluationConfiguration(MinePactScriptEvalConfig) {
            constructorArgs(api)
        }

        return when (val result = host.evaluator(compiled, evalConfig)) {
            is ResultWithDiagnostics.Success ->
                Result.success(result.value.returnValue.scriptInstance)
            is ResultWithDiagnostics.Failure -> {
                val errors = result.reports.joinToString("\n") { "  ${it.message}" }
                Result.failure(ScriptEvaluationException(errors))
            }
        }
    }

    fun clearCache() {
        cache.clear()
        logger.fine("[ScriptEngine] Compilation cache cleared.")
    }
    fun cacheSize() = cache.size

    private fun File.sha256(): String =
        MessageDigest.getInstance("SHA-256")
            .digest(readBytes())
            .joinToString("") { "%02x".format(it) }
}