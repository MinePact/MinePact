package net.minepact.core.global.commands

import net.minepact.Main
import net.minepact.api.command.Command
import net.minepact.api.command.CommandUsage
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Argument
import net.minepact.api.data.repository.ServerRepository
import net.minepact.api.messages.send
import net.minepact.api.server.ServerInfo
import net.minepact.core.global.configs.ServerConfig
import org.bukkit.command.CommandSender
import java.util.concurrent.CompletableFuture

class ServerInfoCommand : Command(
    name = "server-info",
    description = "Retrieve info for any given server.",
    permission = "minepact.admin.server-info",
    aliases = mutableListOf("s-info", "si"),
    usage = CommandUsage(
        label = "server-info", arguments = listOf()
    ),
    cooldown = 1.0
) {
    override fun execute(
        sender: CommandSender, args: MutableList<Argument<*>>
    ): Result {
        val serverNames: List<String> = if (args.isEmpty()) {
            val allServers = ServerRepository.findAll().get()
            allServers.map { it.name }
        } else {
            args.map { it.value as String }
        }

        val futures: List<CompletableFuture<List<ServerInfo>>> = serverNames.map { name -> ServerRepository.findByName(name) }

        CompletableFuture.allOf(*futures.toTypedArray()).thenAccept {
            futures.forEach { future ->
                future.get().forEach { serverInfo ->
                    sender.send("")
                    val msg = "| Name: ${serverInfo.name}\n| Type: ${serverInfo.type}\n| Staging: ${serverInfo.staging}\n| UUID: ${serverInfo.uuid}"
                    sender.send(msg)
                }
            }
        }

        return Result.SUCCESS
    }
}