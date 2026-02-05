package net.minepact.api.reflections

import net.minepact.api.command.Command
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.lang.reflect.Modifier
import java.util.jar.JarFile

fun findCommands(
    plugin: JavaPlugin,
    basePackage: String
): List<Command> {
    val commands = mutableListOf<Command>()
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
                    val clazz = Class.forName(className)
                    if (
                        Command::class.java.isAssignableFrom(clazz) &&
                        !clazz.isInterface &&
                        !Modifier.isAbstract(clazz.modifiers)
                    ) {
                        val instance = clazz
                            .getDeclaredConstructor()
                            .apply { isAccessible = true }
                            .newInstance() as Command
                        commands.add(instance)
                    }
                } catch (_: Throwable) {}
            }
    }
    return commands
}