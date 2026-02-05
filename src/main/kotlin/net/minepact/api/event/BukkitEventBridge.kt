package net.minepact.api.event

import net.minepact.Main
import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import java.io.File
import java.lang.reflect.Modifier
import java.util.jar.JarFile

class BukkitEventBridge(
    private val eventRegister: EventRegister
) : Listener {
    fun registerAllEvents() {
        val eventClasses = getAllBukkitEvents()
        for (eventClass in eventClasses) {
            try {
                if (Modifier.isAbstract(eventClass.modifiers)) continue
                if (eventClass.name.contains("org.bukkit.event.player.PlayerLoginEvent")) continue

                Bukkit.getPluginManager().registerEvent(
                    eventClass, this, EventPriority.NORMAL,
                    { _, event -> eventRegister.call(event) },
                    Main.instance, true
                )
            } catch (ex: Throwable) {
                Main.instance.logger.warning("Failed to register bridge for ${eventClass.name}: ${ex.message}")
            }
        }
    }

    private fun getAllBukkitEvents(): List<Class<out Event>> {
        val classes = mutableListOf<Class<out Event>>()
        val packageName = "org.bukkit.event"
        val basePath = packageName.replace('.', '/')
        val jarFile = JarFile(File(Bukkit::class.java.protectionDomain.codeSource.location.toURI()))

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
                        if (Event::class.java.isAssignableFrom(clazz) &&
                            !clazz.isInterface &&
                            !Modifier.isAbstract(clazz.modifiers)
                        ) {
                            @Suppress("UNCHECKED_CAST")
                            classes.add(clazz as Class<out Event>)
                        }
                    } catch (_: Throwable) {
                    }
                }
        }

        return classes
    }
}