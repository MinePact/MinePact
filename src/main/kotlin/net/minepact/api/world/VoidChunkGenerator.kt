package net.minepact.api.world

import net.minepact.Main
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.generator.ChunkGenerator
import java.util.Random

@Suppress("DEPRECATION")
class VoidChunkGenerator : ChunkGenerator() {
    @Suppress("OVERRIDE_DEPRECATION")
    override fun generateChunkData(
        world: World,
        random: Random,
        x: Int,
        z: Int,
        biome: BiomeGrid
    ): ChunkData {
        val data = createChunkData(world)

        if (x == 0 && z == 0) {
            try {
                val y = 64
                data.setBlock(0, y, 0, Material.BEDROCK)

                Bukkit.getScheduler().runTask(Main.instance, Runnable {
                    try {
                        world.setSpawnLocation(0, y + 1, 0)
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                })
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        return data
    }
}
