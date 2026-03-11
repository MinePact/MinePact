package net.minepact.core.global.commands

import kotlinx.coroutines.future.asCompletableFuture
import net.minepact.Main
import net.minepact.api.command.Command
import net.minepact.api.command.CommandUsage
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Argument
import net.minepact.api.command.arguments.ExpectedArgument
import net.minepact.api.player.Player
import net.minepact.api.permissions.Permission
import net.minepact.api.scripts.ScriptManager
import net.minepact.api.server.ServerType
import java.util.concurrent.CompletableFuture

class ScriptCommand : Command(
    server = ServerType.GLOBAL,
    name = "script",
    description = "Manage the server's custom scripts",
    aliases = mutableListOf("scr", "scripts"),
    permission = Permission("minepact.admin.scripts"),
    usage = CommandUsage(label = "script", arguments = listOf(
        ExpectedArgument(name = "action", potentialValues = listOf("reload", "list", "info"))
    )),
    cooldown = 0.5,
    log = true,
    maxArgs = Int.MAX_VALUE
) {
    override fun execute(
        sender: Player,
        args: MutableList<Argument<*>>
    ): Result {
        val scriptManager: ScriptManager = Main.SCRIPT_MANAGER
        when (val action = args[0].value as String) {
            "reload" -> {
                updateUsage(args)

                val hard = args[1].value == "--hard"
                sender.sendMessage("Reloading scripts${if (hard) " (hard)" else ""}...")
                val start = System.currentTimeMillis()

                Main.instance.async {
                    scriptManager.reload(clearCache = hard)
                }.asCompletableFuture()
                    .thenAcceptAsync({
                        val elapsed = System.currentTimeMillis() - start
                        sender.sendMessage(
                            "Done in ${elapsed}ms. Loaded: ${scriptManager.loadedScripts().size} scripts."
                        )
                    }, Main.instance.mainThreadExecutor)

                val elapsed = System.currentTimeMillis() - start
                sender.sendMessage("Done in ${elapsed}ms. " + "Loaded: ${scriptManager.loadedScripts().size} scripts.")
            }
            "list" -> {
                updateUsage(args)

                val scripts  = scriptManager.loadedScripts().sorted()
                val services = scriptManager.registeredServices()
                sender.sendMessage("Scripts (${scripts.size}): ${scripts.joinToString()}")
                sender.sendMessage("Services (${services.size}): ${services.joinToString()}")
            }
            "info" -> {
                updateUsage(args)

                val name = args[1].value as String
                val loaded = name in scriptManager.loadedScripts()
                sender.sendMessage("Script '$name': ${if (loaded) "✓ loaded" else "✗ not loaded"}")
            }
            else -> {
                sender.sendMessage("Unknown action '$action'. Valid actions are: reload, list, info.")
                return Result.FAILURE
            }
        }

        return Result.SUCCESS
    }

    private fun updateUsage(args: MutableList<Argument<*>>) {
        this.usage = if ((args[0].value as String) == "reload") CommandUsage(label = "script", arguments = listOf(
            ExpectedArgument(name = "action", potentialValues = listOf("reload", "list", "info")),
            ExpectedArgument(name = "option", potentialValues = listOf("--hard"))
        ))
        else if ((args[0].value as String) == "info") CommandUsage(label = "script", arguments = listOf(
            ExpectedArgument(name = "action", potentialValues = listOf("reload", "list", "info")),
            ExpectedArgument(name = "option", Main.SCRIPT_MANAGER.loadedScripts().filter { it.startsWith(args[1].value as String) }.toList())
        ))
        else CommandUsage(label = "script", arguments = listOf(
            ExpectedArgument(name = "action", potentialValues = listOf("reload", "list", "info"))
        ))
    }
}