package net.minepact.api.command.scripts

import kotlinx.coroutines.runBlocking
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.*
import kotlin.script.experimental.jvm.*
import kotlin.script.experimental.dependencies.*
import kotlin.script.experimental.dependencies.maven.MavenDependenciesResolver
import kotlin.script.experimental.jvm.JvmScriptEvaluationConfigurationBuilder.Companion.invoke

@KotlinScript(
    fileExtension = "cmd.kts",
    compilationConfiguration = CommandScriptConfiguration::class
)
class CommandScript
object CommandScriptConfiguration : ScriptCompilationConfiguration({
    defaultImports(
        DependsOn::class,
        Repository::class,

    )

    jvm {
        dependenciesFromClassloader(
            classLoader = CommandScript::class.java.classLoader,
            wholeClasspath = true
        )

    }

    refineConfiguration {
        onAnnotations(
            DependsOn::class,
            Repository::class,
            handler = ::configureCommandDependencies
        )
    }

    compilerOptions(
        "-jvm-target=21"
    )
}) {
    private fun readResolve(): Any = CommandScriptConfiguration
}

private val resolver = CompoundDependenciesResolver(FileSystemDependenciesResolver(), MavenDependenciesResolver())

fun configureCommandDependencies(context: ScriptConfigurationRefinementContext): ResultWithDiagnostics<ScriptCompilationConfiguration> {
    val annotations = context.collectedData?.get(ScriptCollectedData.collectedAnnotations)?.takeIf { it.isNotEmpty() }
        ?: return context.compilationConfiguration.asSuccess()
    return runBlocking { resolver.resolveFromScriptSourceAnnotations(annotations) }
        .onSuccess { context.compilationConfiguration.with {
            dependencies.append(JvmDependency(it))
        }.asSuccess()
    }
}