package net.minepact.core.global.commands

import net.minepact.api.command.dsl.Command
import net.minepact.api.command.Provider
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.ArgumentInputType
import net.minepact.api.permissions.Permission

class TestCommand : Command() {
    init {
        command("test") {
            description = "Test a command"
            permission = Permission("minepact.test")

            executes { sender, _ ->
                sender.sendMessage("Test command executed!")
                Result.SUCCESS
            }

            subcommand("sub1") {
                argument("player", dynamicProvider = Provider.PLAYERS) {
                    subcommand("add") {
                        argument("permission", inputType = ArgumentInputType.STRING) {
                            executes { sender, args ->
                                val player = args[0].value as String
                                val perm = args[1].value as String

                                sender.sendMessage("add")
                                sender.sendMessage(player)
                                sender.sendMessage(perm)
                                Result.SUCCESS
                            }
                        }
                    }
                    subcommand("remove") {
                        argument("permission", inputType = ArgumentInputType.STRING) {
                            executes { sender, args ->
                                sender.sendMessage("rem")
                                sender.sendMessage(args[0].value as String)
                                sender.sendMessage(args[1].value as String)

                                Result.SUCCESS
                            }
                        }
                    }
                }
            }
        }
    }
}