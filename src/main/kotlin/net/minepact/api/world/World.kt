package net.minepact.api.world

import org.bukkit.Bukkit
import java.io.File
import java.util.UUID
import org.bukkit.configuration.file.YamlConfiguration

/**
 * A lightweight, persistable representation of a world in MinePact.
 * This is distinct from org.bukkit.World (the runtime world) and contains
 * the metadata we persist about worlds.
 */
data class World(
    var id: UUID = UUID.randomUUID(),
    var name: String = "world",
    var generation: WorldGenerationType = WorldGenerationType.NORMAL,
    var environment: String = org.bukkit.World.Environment.NORMAL.name,
    var seed: Long? = null,
    var generatorSettings: String? = null
) {
    fun asBukkitWorld(): org.bukkit.World? = Bukkit.getWorld(name)

    fun saveToFile(dir: File) {
        val file = File(dir, "$id.yml")
        dir.mkdirs()
        val yaml = YamlConfiguration()
        yaml.set("id", id.toString())
        yaml.set("name", name)
        yaml.set("generation", generation.name)
        yaml.set("environment", environment)
        yaml.set("seed", seed)
        yaml.set("generatorSettings", generatorSettings)
        yaml.save(file)
    }

    companion object {
        fun fromFile(file: File): World? {
            return try {
                val yaml = YamlConfiguration.loadConfiguration(file)
                val id = UUID.fromString(yaml.getString("id") ?: return null)
                val name = yaml.getString("name") ?: return null
                val generation = try {
                    WorldGenerationType.valueOf(yaml.getString("generation") ?: WorldGenerationType.NORMAL.name)
                } catch (_: Exception) { WorldGenerationType.NORMAL }
                val environment = yaml.getString("environment") ?: org.bukkit.World.Environment.NORMAL.name
                val seed: Long? = when (val raw = yaml.get("seed")) {
                    is Number -> raw.toLong()
                    is String -> raw.toLongOrNull()
                    else -> null
                }
                val generatorSettings = yaml.getString("generatorSettings")
                World(id, name, generation, environment, seed, generatorSettings)
            } catch (ex: Exception) {
                ex.printStackTrace()
                null
            }
        }
    }
}