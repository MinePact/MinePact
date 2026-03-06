package net.minepact.api.scripts.bootstrap

import kotlin.script.experimental.api.ScriptAcceptedLocation
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.acceptedLocations
import kotlin.script.experimental.api.baseClass
import kotlin.script.experimental.api.compilerOptions
import kotlin.script.experimental.api.defaultImports
import kotlin.script.experimental.api.ide
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvm.updateClasspath
import kotlin.script.experimental.jvm.util.classpathFromClassloader

object BootstrapCompilationConfig : ScriptCompilationConfiguration({
    baseClass(BootstrapScript::class)
    defaultImports("net.minepact.api.bootstrap.*")
    jvm {
        val classLoader = BootstrapScript::class.java.classLoader
        val classpath = classpathFromClassloader(classLoader)
            ?: error("[ScriptEngine] Could not resolve classpath for bootstrap")
        updateClasspath(classpath)
    }
    ide { acceptedLocations(ScriptAcceptedLocation.Everywhere) }
    compilerOptions("-jvm-target", "21")
}) {
    private fun readResolve(): Any = BootstrapCompilationConfig
}