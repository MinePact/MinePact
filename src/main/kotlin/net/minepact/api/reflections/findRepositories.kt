package net.minepact.api.reflections

import net.minepact.Main
import net.minepact.api.data.repository.Repository
import java.io.File
import java.lang.reflect.Modifier
import java.util.jar.JarFile
import kotlin.sequences.forEach

@Suppress("UNCHECKED_CAST")
fun findRepositories(basePackage: String): List<Repository<*>> {
    val events = mutableListOf<Repository<*>>()
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
                    val clazz = Class.forName(className) as Class<Repository<*>>
                    if (Repository::class.java.isAssignableFrom(clazz) &&
                        !clazz.isInterface &&
                        !Modifier.isAbstract(clazz.modifiers)
                    ) {
                        val instance = clazz.getDeclaredConstructor()
                            .apply { isAccessible = true }
                            .newInstance() as Repository<*>
                        events.add(instance)
                    }
                } catch (_: Throwable) {}
            }
    }
    return events
}
