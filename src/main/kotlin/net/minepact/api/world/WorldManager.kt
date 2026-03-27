package net.minepact.api.world

import net.minepact.Main
import org.bukkit.Bukkit
import org.bukkit.WorldCreator
import org.bukkit.WorldType
import java.io.File
import java.util.UUID
import kotlin.random.Random

object WorldManager {
    private val worlds = mutableMapOf<UUID, World>()
    private val worldsDir: File
        get() = File(Main.instance.dataFolder, "worlds")

    fun create(name: String, generation: WorldGenerationType = WorldGenerationType.NORMAL, environment: org.bukkit.World.Environment = org.bukkit.World.Environment.NORMAL, seed: Long? = null, generatorSettings: String? = null): World {
        get(name)?.let { return it }

        val finalSeed = seed ?: Random.nextLong()

        val w = World(UUID.randomUUID(), name, generation, environment.name, finalSeed, generatorSettings)
        w.saveToFile(worldsDir)
        worlds[w.id] = w
        ensureLoaded(w)
        return w
    }

    fun createOverworld(name: String, generation: WorldGenerationType = WorldGenerationType.NORMAL, seed: Long? = null, generatorSettings: String? = null) =
        create(name, generation, org.bukkit.World.Environment.NORMAL, seed, generatorSettings)
    fun createNether(name: String, generation: WorldGenerationType = WorldGenerationType.NORMAL, seed: Long? = null, generatorSettings: String? = null) =
        create(name, generation, org.bukkit.World.Environment.NETHER, seed, generatorSettings)
    fun createEnd(name: String, generation: WorldGenerationType = WorldGenerationType.NORMAL, seed: Long? = null, generatorSettings: String? = null) =
        create(name, generation, org.bukkit.World.Environment.THE_END, seed, generatorSettings)

    fun createSuperflat(name: String, generatorSettings: String, biome: String? = null, seed: Long? = null): World {
        val gs = if (biome != null) "$generatorSettings;$biome" else generatorSettings
        return create(name, WorldGenerationType.FLAT, org.bukkit.World.Environment.NORMAL, seed, gs)
    }

    fun createSuperflatFromLayers(name: String, layers: List<Pair<String, Int>>, biome: String? = null, seed: Long? = null): World {
        // Build a simple layers string. Format: `count*blockName,...` (e.g. "1*minecraft:bedrock,2*minecraft:dirt,1*minecraft:grass_block")
        val layersStr = layers.joinToString(",") { (block, count) -> if (count > 1) "${count}*${block}" else block }
        val gs = if (biome != null) "$layersStr;$biome" else layersStr
        return create(name, WorldGenerationType.FLAT, org.bukkit.World.Environment.NORMAL, seed, gs)
    }

    private fun ensureLoaded(w: World) {
        if (Bukkit.getWorld(w.name) != null) return

        val env = try {
            org.bukkit.World.Environment.valueOf(w.environment)
        } catch (_: Exception) {
            org.bukkit.World.Environment.NORMAL
        }

        val type = try {
            WorldType.valueOf(w.generation.name)
        } catch (_: Exception) {
            // Fallbacks for types we don't have: treat VOID as FLAT, else NORMAL
            if (w.generation == WorldGenerationType.VOID) WorldType.FLAT else WorldType.NORMAL
        }

        val creator = WorldCreator(w.name).environment(env).type(type)
        if (w.seed != null) creator.seed(w.seed!!)
        w.generatorSettings?.let { creator.generatorSettings(it) }
        if (w.generation == WorldGenerationType.VOID) {
            creator.generator(VoidChunkGenerator())
        }

        try {
            // createWorld() causes Bukkit to load/create the world so it's available in-game
            creator.createWorld()
            Main.instance.logger.info("Loaded world: ${w.name} [${w.id}] (env=$env, gen=${w.generation}, seed=${w.seed}, gs=${w.generatorSettings})")
        } catch (ex: Exception) {
            Main.instance.logger.warning("Failed to create/load world ${w.name}: ${ex.message}")
            ex.printStackTrace()
        }
    }

    fun loadAll() {
        worldsDir.mkdirs()
        worlds.clear()

        val files = worldsDir.listFiles { f -> f.isFile && f.extension == "yml" } ?: emptyArray()
        for (f in files) {
            try {
                World.fromFile(f)?.let { w ->
                    worlds[w.id] = w
                    ensureLoaded(w)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        for (bukkitWorld in Bukkit.getWorlds()) {
            if (get(bukkitWorld.name) != null) continue

            val env = bukkitWorld.environment
            val gen = WorldGenerationType.NORMAL
            val seed = try { bukkitWorld.seed } catch (_: Exception) { null }
            val w = World(UUID.randomUUID(), bukkitWorld.name, gen, env.name, seed, null)
            worlds[w.id] = w
            w.saveToFile(worldsDir)
        }
    }
    fun saveAll() {
        worlds.values.forEach { it.saveToFile(worldsDir) }
    }

    fun get(name: String): World? = worlds.values.find { it.name == name }
    fun get(id: UUID): World? = worlds[id]
    fun all(): Collection<World> = worlds.values.toList()

    fun delete(name: String, removeFiles: Boolean = false): Boolean {
        val w = get(name) ?: return false

        // Attempt to unload the Bukkit world if loaded
        try {
            val bukkitWorld = Bukkit.getWorld(w.name)
            if (bukkitWorld != null) {
                Bukkit.unloadWorld(bukkitWorld, true)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        // Remove metadata file
        try {
            val file = File(worldsDir, "${w.id}.yml")
            if (file.exists()) file.delete()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        // Optionally remove world folder from server dir (dangerous)
        if (removeFiles) {
            try {
                val worldFolder = File(Main.instance.server.worldContainer, w.name)
                if (worldFolder.exists()) {
                    worldFolder.deleteRecursively()
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        worlds.remove(w.id)
        return true
    }
}
