package net.minepact.api.reflections

import net.minepact.Main
import net.minepact.api.event.EventHandler
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.lang.reflect.Modifier
import java.util.jar.JarFile

fun findEvents(plugin: JavaPlugin, basePackage: String): List<EventHandler<*>> {
    val events = mutableListOf<EventHandler<*>>()
    val basePath = basePackage.replace('.', '/')

    val jarFile = JarFile(File(plugin.javaClass.protectionDomain.codeSource.location.toURI()))
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
                    val clazz = Class.forName(className) as Class<EventHandler<*>>
                    if (EventHandler::class.java.isAssignableFrom(clazz) &&
                        !clazz.isInterface &&
                        !Modifier.isAbstract(clazz.modifiers)
                    ) {
                        val instance = Main.EVENT_REGISTRY.INSTANCES.getOrPut(clazz) {
                            clazz.getDeclaredConstructor()
                                .apply { isAccessible = true }
                                .newInstance() as EventHandler<*>
                        }
                        events.add(instance)
                    }
                } catch (_: Throwable) {
                }
            }
    }
    return events
}