package net.minepact.api.reflections

import net.minepact.Main
import net.minepact.api.config.experimental.ConfigurationFile
import net.minepact.api.config.experimental.ConfigurationRegistry
import java.io.File
import java.lang.reflect.Modifier
import java.util.jar.JarFile
import kotlin.reflect.KClass
import kotlin.sequences.forEach

fun registerConfigs(basePackage: String): List<ConfigurationFile> {
    val events = mutableListOf<ConfigurationFile>()
    val basePath = basePackage.replace('.', '/')

    val jarFile = JarFile(File(Main.instance.javaClass.protectionDomain.codeSource.location.toURI()))
    jarFile.use { jar ->
        jar.entries().asSequence()
            .filter { it.name.endsWith(".class") }
            .filter { it.name.startsWith(basePath) }
            .forEach { entry ->
                val className = entry.name
                    .removeSuffix(".class")
                    .replace('/', '.')

                try {
                    @Suppress("UNCHECKED_CAST")
                    val clazz: KClass<*> = (Class.forName(className) as Class<*>).kotlin
                    if (ConfigurationFile::class.java.isAssignableFrom(clazz.java) &&
                        !clazz.java.isInterface &&
                        !Modifier.isAbstract(clazz.java.modifiers)
                    ) {
                        @Suppress("UNCHECKED_CAST")
                        val typeInferredClass: KClass<ConfigurationFile> = clazz as KClass<ConfigurationFile>
                        ConfigurationRegistry.register(clazz = typeInferredClass)
                        Main.instance.logger.info("[ConfigurationRegister] Registered config: ${clazz.qualifiedName}")
                    }
                } catch (_: Throwable) {
                }
            }
    }
    return events
}