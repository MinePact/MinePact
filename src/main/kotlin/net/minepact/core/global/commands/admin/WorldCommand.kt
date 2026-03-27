package net.minepact.core.global.commands.admin

import net.minepact.api.command.Result
import net.minepact.api.command.arguments.ArgumentInputType
import net.minepact.api.command.dsl.Command
import net.minepact.api.math.helper.vector.vec
import net.minepact.api.permissions.Permission
import net.minepact.api.world.Position
import net.minepact.api.world.World
import net.minepact.api.world.WorldGenerationType
import net.minepact.api.world.WorldManager
import org.bukkit.Bukkit
import org.bukkit.World as BukkitWorld
import java.util.UUID

class WorldCommand : Command() {
    init {
        command("world") {
            description = "Manage the server's worlds."
            permission = Permission("minepact.admin.world")
            aliases = mutableListOf("world-manager", "worlds")

            subcommand("create") {
                argument(name = "name", optional = false) {
                    argument(name = "generation", potentialValues = WorldGenerationType.entries.map { it.name.lowercase() }, optional = true) {
                        argument(name = "environment", potentialValues = listOf("normal", "nether", "end"), optional = true) {
                            argument(name = "seed", inputType = ArgumentInputType.LONG, optional = true) { executes { sender, args ->
                                val name: String = args.getOrNull(0)?.value as? String ?: run {
                                    sender.sendMessage("<red>World name required.")
                                    return@executes Result.FAILURE
                                }

                                val sGenerationType: String = args.getOrNull(1)?.value as? String ?: "normal"
                                val sEnvironment: String = args.getOrNull(2)?.value as? String ?: "normal"
                                val seed: Long? = args.getOrNull(3)?.value as? Long

                                val generation = try {
                                    WorldGenerationType.valueOf(sGenerationType.uppercase())
                                } catch (ex: Exception) {
                                    sender.sendMessage("<red>Unknown generation type. [<white>${WorldGenerationType.entries.joinToString(separator = "<red>, <white>")}<red>]")
                                    return@executes Result.FAILURE
                                }

                                val environment = try {
                                    BukkitWorld.Environment.valueOf(sEnvironment.uppercase())
                                } catch (ex: Exception) {
                                    sender.sendMessage("<red>Unknown environment. [<white>${BukkitWorld.Environment.entries.joinToString(separator = "<red>, <white>")}<red>]")
                                    return@executes Result.FAILURE
                                }

                                val world: World = WorldManager.create(
                                    name = name,
                                    generation = generation,
                                    environment = environment,
                                    seed = seed
                                )

                                sender.sendMessage("<green>Created world <white>${world.name}<green>! <grey>(${world.id})")
                                return@executes Result.SUCCESS
                            } }
                        }
                    }
                }
            }
            subcommand("delete") { // world delete <name>
                argument("name", potentialValues = WorldManager.all().map { it.name.lowercase() }, optional = false) { executes { sender, args ->
                    val name = args.getOrNull(0)?.value as? String ?: run {
                        sender.sendMessage("<red>World name required.")
                        return@executes Result.FAILURE
                    }

                    val exists = WorldManager.get(name)
                    if (exists == null) {
                        sender.sendMessage("<red>Couldn't find '<white>$name<red>'.")
                        return@executes Result.FAILURE
                    }

                    val success = WorldManager.delete(name, true)
                    if (success) {
                        sender.sendMessage("<green>Deleted world <white>$name<green>.")
                        return@executes Result.SUCCESS
                    } else {
                        sender.sendMessage("<red>Failed to delete world <white>$name<red>.")
                        return@executes Result.FAILURE
                    }
                } }
            }
            subcommand("teleport") { // world teleport <name>
                argument("name", potentialValues = WorldManager.all().map { it.name.lowercase() }, optional = false) { executes { sender, args ->
                    val name = args.getOrNull(0)?.value as? String ?: run {
                        sender.sendMessage("<red>World name required.")
                        return@executes Result.FAILURE
                    }

                    val w = WorldManager.get(name)
                    if (w == null) {
                        sender.sendMessage("<red>Couldn't find '<white>$name<red>'.")
                        return@executes Result.FAILURE
                    }

                    sender.teleport(Position(vec(0, 60, 0), 0f, 0f, w.name))
                    sender.sendMessage("<yellow>Teleporting...")
                    return@executes Result.SUCCESS
                } }
            }
            subcommand("list") { executes { sender, _ ->
                val all = WorldManager.all()
                if (all.isEmpty()) {
                    sender.sendMessage("<yellow>No worlds are registered.")
                    return@executes Result.SUCCESS
                }

                sender.sendMessage("<green>Registered worlds (<white>${all.size}<green>):")
                for (w in all) {
                    sender.sendMessage("<white>${w.name} <grey>(${w.id}) <dark_grey>- env=${w.environment.lowercase()}, gen=${w.generation.name.lowercase()}, seed=${w.seed}")
                }
                return@executes Result.SUCCESS
            } }
            subcommand("info") { // world info <name|uuid>
                argument("target", potentialValues = WorldManager.all().map { it.name.lowercase() }, optional = false) { executes { sender, args ->
                    val target = args.getOrNull(0)?.value as? String ?: run {
                        sender.sendMessage("<red>World name or UUID required.")
                        return@executes Result.FAILURE
                    }

                    var w = WorldManager.get(target)
                    if (w == null) w = try { WorldManager.get(UUID.fromString(target)) } catch (_: Exception) { null }
                    if (w == null) {
                        sender.sendMessage("<red>No world found for '<white>$target<red>'.")
                        return@executes Result.FAILURE
                    }

                    sender.sendMessage("<green>World Info for <white>${w.name}<green>:")
                    sender.sendMessage("<white>ID: <grey>${w.id}")
                    sender.sendMessage("<white>Environment: <grey>${w.environment}")
                    sender.sendMessage("<white>Generation: <grey>${w.generation.name.lowercase()}")
                    sender.sendMessage("<white>Seed: <grey>${w.seed}")
                    sender.sendMessage("<white>Generator Settings: <grey>${w.generatorSettings ?: "(none)"}")

                    val bukkitWorld = Bukkit.getWorld(w.name)
                    if (bukkitWorld != null) {
                        val spawn = bukkitWorld.spawnLocation
                        sender.sendMessage("<white>Loaded: <green>Yes <white>Spawn: <grey>(${spawn.blockX}, ${spawn.blockY}, ${spawn.blockZ})")
                    } else {
                        sender.sendMessage("<white>Loaded: <red>No")
                    }

                    return@executes Result.SUCCESS
                } }
            }
        }
    }
}