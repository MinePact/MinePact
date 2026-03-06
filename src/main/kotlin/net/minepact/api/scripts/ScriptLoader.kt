package net.minepact.api.scripts

import java.io.File
import java.util.logging.Logger

class ScriptLoader(
    private val compiler: ScriptCompiler,
    private val logger: Logger
) {
    private val dependsOnRegex = Regex("""@file:DependsOn\s*\(\s*([^)]+)\)""")
    private val scriptNameRegex = Regex("""@file:ScriptName\s*\(\s*"([^"]+)"\s*\)""")

    fun discover(dir: File): List<File> =
        dir.listFiles { f ->
            f.isFile &&
                    f.name.endsWith(".minepact.kts") &&
                    f.name != "bootstrap.minepact.kts"
        }?.sortedBy { it.name } ?: emptyList()


    fun nameOf(file: File): String {
        val text = file.readText()
        return scriptNameRegex.find(text)?.groupValues?.get(1)
            ?: file.name.removeSuffix(".minepact.kts")
    }
    fun dependenciesOf(file: File): Set<String> {
        val text = file.readText()
        return dependsOnRegex.findAll(text).flatMap { match ->
            match.groupValues[1]
                .split(",")
                .map { it.trim().removeSurrounding("\"") }
                .filter { it.isNotEmpty() }
        }.toSet()
    }

    suspend fun load(file: File, api: ScriptAPI): Result<Any?> {
        val compiled = compiler.compile(file).getOrElse { return Result.failure(it) }
        return compiler.evaluate(compiled, api)
    }
}