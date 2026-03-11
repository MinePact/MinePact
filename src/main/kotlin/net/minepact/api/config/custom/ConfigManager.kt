package net.minepact.api.config.custom

import net.minepact.Main
import net.minepact.api.config.custom.exception.ConfigException
import net.minepact.api.config.custom.type.minepact.MinePactFile
import java.io.File
import java.util.concurrent.ConcurrentHashMap

object ConfigManager {
    private val cache = ConcurrentHashMap<String, MinePactFile>()

    inline fun <reified T : ConfigType> file(path: String): MinePactFile {
        val type = when (T::class) {
            ConfigType.MinePact::class  -> ConfigType.MinePact
            ConfigType.Json::class -> ConfigType.Json
            ConfigType.Yaml::class -> ConfigType.Yaml
            else -> throw ConfigException("Unknown ConfigType: ${T::class.simpleName}")
        }
        return load(path, type)
    }

    fun load(path: String, type: ConfigType): MinePactFile {
        return cache.getOrPut(path) { readFromDisk(path, type) }
    }
    fun reload(path: String): MinePactFile {
        val existing = cache[path] ?: throw ConfigException("Cannot reload '$path': it was never loaded. Use load() first.")
        val fresh = readFromDisk(path, existing.type)
        cache[path] = fresh
        return fresh
    }

    fun unload(path: String) { cache.remove(path) }
    fun unloadAll() { cache.clear() }
    fun isLoaded(path: String): Boolean = cache.containsKey(path)
    fun loadedPaths(): Set<String> = cache.keys.toSet()

    private fun readFromDisk(path: String, type: ConfigType): MinePactFile {
        val ioFile = File("${Main.instance.dataFolder}/$path")
        val content = if (ioFile.exists()) ioFile.readText() else {
            ioFile.parentFile?.mkdirs()
            ioFile.createNewFile()
            ""
        }
        val parser = type.createParser()
        val data = parser.parse(content).toMutableMap()
        val reader = type.createReader(data)
        val writer = type.createWriter(data, ioFile)
        return MinePactFile(path, type, reader, writer)
    }
}