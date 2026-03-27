package net.minepact.core.global.commands.admin

import net.minepact.api.command.Result
import net.minepact.api.command.arguments.ArgumentInputType
import net.minepact.api.command.arguments.Arguments
import net.minepact.api.command.arguments.IntegerArgumentRange
import net.minepact.api.command.dsl.Command
import net.minepact.api.permissions.Permission

class GiveCommand : Command() {
    init {
        command("give") {
            description = "Give an item to a player."
            permission = Permission("minepact.admin.give")

            argument(Arguments.PLAYERS_REQUIRED) {
                argument("item", , optional = false) {
                    argument("amount", range = IntegerArgumentRange(1, 64), inputType = ArgumentInputType.INTEGER, optional = true) {
                        executes { sender, args ->
                            val target = args[0].value
                            val itemName = args[1].value as String
                            val amount = args.getOrNull(2)?.value as? Int ?: 1



                            Result.SUCCESS
                        }
                    }
                }
            }
        }
    }
}