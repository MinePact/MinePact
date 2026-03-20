package net.minepact.core.global.commands.admin

import net.minepact.api.command.Provider
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.ArgumentInputType
import net.minepact.api.command.arguments.Arguments
import net.minepact.api.command.dsl.Command
import net.minepact.api.data.repository.GroupRepository
import net.minepact.api.permissions.Group
import net.minepact.api.permissions.GroupRegistry
import net.minepact.api.permissions.Permission

class PermissionCommand : Command() {
    init {
        command("permission") {
            description = "A command to manage permissions."
            permission = Permission("minepact.admin.permissions")
            aliases = mutableListOf("perm", "perms")

            subcommand("group") {
                subcommand("create") {
                    argument("name", inputType = ArgumentInputType.STRING) { executes { sender, args ->
                        val group = Group(
                            name = args[0].value as String,
                            displayName = args[0].value as String,
                            weight = 0
                        )
                        
                        if (GroupRegistry.all().map { it.name }.contains(group.name)) {
                            sender.sendMessage("<white>${args[0].value as String} <red>already exists.")
                            return@executes Result.SUCCESS
                        }
                        
                        GroupRegistry.register(group)
                        GroupRepository.insert(group)

                        sender.sendMessage("<green>Created the group <white>${args[0].value as String}<green>.")
                        Result.SUCCESS
                    } }
                }
                subcommand("delete") {
                    argument("name", inputType = ArgumentInputType.STRING) { executes { sender, args ->
                        val group: Group? = GroupRegistry.get(name).get()
                        if (group == null) {
                            sender.sendMessage("<red>The group <white>${args[0].value as String} <red>does not exist.")
                            return@executes Result.SUCCESS
                        }
                        GroupRegistry.unregister(group)
                        GroupRepository.delete(group)

                        sender.sendMessage("<red>Deleted the group <white>${args[0].value as String}<red>.")
                        Result.SUCCESS
                    } }
                }
                subcommand("info") {
                    argument("group", inputType = ArgumentInputType.STRING, dynamicProvider = Provider.GROUPS) {
                        executes { sender, args ->
                            val group: Group? = GroupRegistry.get(name).get()
                            if (group == null) {
                                sender.sendMessage("<red>The group <white>${args[0].value as String} <red>does not exist.")
                                return@executes Result.SUCCESS
                            }

                            sender.sendMessage("<strikethrough><red>--------------------------</red></strikethrough>")
                            sender.sendMessage("<green>Group: <white>${group.name}")
                            sender.sendMessage("<yellow>| <green>Display Name: <white>${group.displayName ?: "None"}")
                            sender.sendMessage("<yellow>| <green>Weight: <white>${group.weight}")
                            sender.sendMessage("<yellow>| <green>Prefix: <white>${group.prefix ?: "None"}")
                            sender.sendMessage("<yellow>| <green>Suffix: <white>${group.suffix ?: "None"}")
                            sender.sendMessage("<yellow>| <green>Permissions:")
                            if (group.permissions.isEmpty()) sender.sendMessage("<yellow>| <green>- <gray>None")
                            else group.permissions.forEach { sender.sendMessage("<yellow>| <green>- ${it.toInfoString()}") }
                            sender.sendMessage("<yellow>| <green>Parents:")
                            if (group.parents.isEmpty()) sender.sendMessage("<yellow>| <green>- <gray>None")
                            else group.parents.forEach { sender.sendMessage("<yellow>| <green>- <white>$it") }
                            sender.sendMessage("<yellow>| <green>Expires: <white>${group.expiresAt ?: "Never"}")
                            sender.sendMessage("<strikethrough><red>--------------------------</red></strikethrough>")
                            Result.SUCCESS
                        }
                    }
                }
                argument("group", inputType = ArgumentInputType.GROUP, dynamicProvider = Provider.GROUPS) {
                    subcommand("perm") {
                        subcommand("add") {
                            argument("permission", inputType = ArgumentInputType.STRING) { executes { sender, args ->
                                val group: Group? = GroupRegistry.get(args[0].value as String).get()
                                if (group == null) {
                                    sender.sendMessage("<red>The group <white>${args[0].value as String} <red>does not exist.")
                                    return@executes Result.SUCCESS
                                }

                                group.permissions.add(Permission(args[1].value as String))
                                sender.sendMessage("<green>Added permission '<white>${args[1].value as String}<green>' to '<white>${group.name}<green>'.")
                                Result.SUCCESS
                            } }
                        }
                        subcommand("remove") {
                            argument("permission", inputType = ArgumentInputType.STRING) { executes { sender, args ->
                                val group: Group? = GroupRegistry.get(args[0].value as String).get()
                                if (group == null) {
                                    sender.sendMessage("<red>The group <white>${args[0].value as String} <red>does not exist.")
                                    return@executes Result.SUCCESS
                                }

                                group.permissions.removeIf { it.node == args[1].value as String }
                                sender.sendMessage("<red>Removed permission '<white>${args[1].value as String}<red>' from group '<white>${group.name}<red>'.")
                                Result.SUCCESS
                            } }
                        }
                        subcommand("edit") {
                            argument("permission", inputType = ArgumentInputType.STRING) {
                                argument("value", inputType = ArgumentInputType.BOOLEAN) { executes { sender, args ->
                                    val group: Group? = GroupRegistry.get(args[0].value as String).get()
                                    if (group == null) {
                                        sender.sendMessage("<red>The group <white>${args[0].value as String} <red>does not exist.")
                                        return@executes Result.SUCCESS
                                    }

                                    group.permissions.removeIf { it.node == args[1].value as String }
                                    sender.sendMessage("<red>Removed permission '<white>${args[1].value as String}<red>' from group '<white>${group.name}<red>'.")
                                    Result.SUCCESS
                                } }
                            }
                        }
                    }
                    subcommand("parent") {
                        subcommand("add") {}
                        subcommand("remove") {}
                    }
                    subcommand("set") {
                        subcommand("display") {}
                        subcommand("weight") {}
                        subcommand("prefix") {}
                        subcommand("suffix") {}

                    }
                    subcommand("reset") {
                        subcommand("display") {}
                        subcommand("weight") {}
                        subcommand("prefix") {}
                        subcommand("suffix") {}
                    }
                }
            }

            subcommand("user") {
                argument(Arguments.PLAYERS_REQUIRED) {
                    subcommand("info") {}
                    subcommand("permission") {}
                    subcommand("group") {}
                }
            }
        }
    }
}