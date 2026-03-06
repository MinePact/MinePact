package net.minepact.api.config.experimental

import net.minepact.Main
import org.bukkit.Bukkit
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

object ConfigurationRegistry {
    data class Entry<T : ConfigurationFile>(
        val clazz: KClass<T>,
        @Volatile var instance: T
    )

    val configs = mutableMapOf<KClass<*>, Entry<*>>()

    fun <T : ConfigurationFile> register(clazz: KClass<T>) {
        val instance = clazz.createInstance()
        ConfigLoader.load(instance)
        configs[clazz] = Entry(clazz, instance)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : ConfigurationFile> get(clazz: KClass<T>): T {
        return (configs[clazz] ?: error("Config not registered!")).instance as T
    }

    fun <T : ConfigurationFile> save(clazz: KClass<T>) {
        val entry = configs[clazz] ?: error("Config not registered!")
        @Suppress("UNCHECKED_CAST")
        ConfigLoader.save(entry.instance as T)
    }

    fun <T : ConfigurationFile> reload(
        clazz: KClass<T>,
        callback: ((Boolean) -> Unit)? = null
    ) {
        reloadAsync(clazz, callback)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : ConfigurationFile> reloadAsync(
        clazz: KClass<T>,
        callback: ((Boolean) -> Unit)? = null
    ) {
        val entry = configs[clazz] as? Entry<T>
            ?: error("Config not registered")

        val oldInstance = entry.instance

        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, Runnable {

            val newInstance = try {
                clazz.createInstance().also {
                    ConfigLoader.load(it)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                Bukkit.getScheduler().runTask(Main.instance, Runnable {
                    callback?.invoke(false)
                })
                return@Runnable
            }

            Bukkit.getScheduler().runTask(
                Main.instance,
                Runnable {
                    entry.instance = newInstance
                    if (newInstance is ReloadableConfig<*>) { (newInstance as ReloadableConfig<T>).onReload(oldInstance) }
                    callback?.invoke(true)
                }
            )
        })
    }
}
