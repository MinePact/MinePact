package net.minepact.core.global.commands.admin

import net.minepact.Main
import net.minepact.api.command.Provider
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.ArgumentInputType
import net.minepact.api.command.arguments.Arguments
import net.minepact.api.command.dsl.Command
import net.minepact.api.data.repository.permissions.GroupRepository
import net.minepact.api.messages.helper.msg
import net.minepact.api.permissions.Group
import net.minepact.api.permissions.GroupRegistry
import net.minepact.api.permissions.Permission
import net.minepact.api.permissions.PermissionScope
import net.minepact.api.player.Player

class PermissionCommand : Command() {
    init {
        command("permission") {
            description = "A command to manage permissions."
            permission = Permission("minepact.admin.permissions")
            aliases = mutableListOf("perm", "perms")

            subcommand("group") {
                subcommand("create") {
                    argument("name", inputType = ArgumentInputType.STRING) { executes { sender, args ->
                        val groupName = args[0].value as String
                        val group = Group(
                            name = groupName,
                            serverId = Main.SERVER.info.uuid.toString(),
                            displayName = groupName,
                            weight = 0
                        )

                        if (GroupRegistry.all().map { it.name }.contains(group.name)) {
                            sender.sendMessage("<white>$groupName <red>already exists.")
                            return@executes Result.SUCCESS
                        }

                        GroupRegistry.register(group)
                        GroupRepository.insert(group)

                        sender.sendMessage("<green>Created the group <white>$groupName<green>.")
                        Result.SUCCESS
                    } }
                }
                subcommand("delete") {
                    argument("name", inputType = ArgumentInputType.STRING) { executes { sender, args ->
                        val groupName = args[0].value as String
                        val group: Group? = GroupRegistry.get(groupName).get()
                        if (group == null) {
                            sender.sendMessage("<red>The group <white>$groupName <red>does not exist.")
                            return@executes Result.SUCCESS
                        }
                        GroupRegistry.unregister(group)
                        GroupRepository.delete(group.name)

                        sender.sendMessage("<red>Deleted the group <white>$groupName<red>.")
                        Result.SUCCESS
                    } }
                }
                subcommand("info") {
                    argument("group", inputType = ArgumentInputType.STRING, dynamicProvider = Provider.GROUPS) {
                        executes { sender, args ->
                            val group: Group? = GroupRegistry.get(args[0].value as String).get()
                            if (group == null) {
                                sender.sendMessage("<red>The group <white>${args[0].value as String} <red>does not exist.")
                                return@executes Result.SUCCESS
                            }

                            sender.sendMessage(msg {
                                +"<green>Group: <white>${group.name}\n"
                                +"<yellow>| <green>Display Name: <white>${group.displayName ?: "None"}\n"
                                +"<yellow>| <green>Weight: <white>${group.weight}\n"
                                +"<yellow>| <green>Prefix: <white>${group.prefix ?: "None"}\n"
                                +"<yellow>| <green>Suffix: <white>${group.suffix ?: "None"}\n"
                                +"<yellow>| <green>Permissions:\n"
                                if (group.permissions.isEmpty()) +"<yellow>| <green>- <gray>None\n"
                                else group.permissions.forEach { +"<yellow>| <green>- ${it.toInfoString()}\n" }
                                +"<yellow>| <green>Parents:\n"
                                if (group.parents.isEmpty()) +"<yellow>| <green>- <gray>None\n"
                                else group.parents.forEach { +"<yellow>| <green>- <white>$it\n" }
                                +"<yellow>| <green>Expires: <white>${group.expiresAt ?: "Never"}\n"
                            })
                            Result.SUCCESS
                        }
                    }
                }

                argument("group", inputType = ArgumentInputType.STRING, dynamicProvider = Provider.GROUPS) {
                    subcommand("permission") {
                        subcommand("add") {
                            argument("permission", inputType = ArgumentInputType.STRING) { executes { sender, args ->
                                val group: Group? = GroupRegistry.get(args[0].value as String).get()
                                if (group == null) {
                                    sender.sendMessage("<red>The group <white>${args[0].value as String} <red>does not exist.")
                                    return@executes Result.SUCCESS
                                }

                                group.permissions.add(Permission(args[1].value as String))
                                GroupRepository.insert(group)
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
                                GroupRepository.insert(group)
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

                                    val permNode = args[1].value as String
                                    val permValue = args[2].value as Boolean

                                    group.permissions.removeIf { it.node == permNode }
                                    group.permissions.add(Permission(permNode, permValue))
                                    GroupRepository.insert(group)

                                    sender.sendMessage("<green>Updated permission '<white>$permNode<green>' for group '<white>${group.name}<green>'.")
                                    Result.SUCCESS
                                } }
                            }
                        }
                    }
                    subcommand("parent") {
                        subcommand("add") {
                            argument("parent", inputType = ArgumentInputType.STRING, dynamicProvider = Provider.GROUPS) { executes { sender, args ->
                                val group: Group? = GroupRegistry.get(args[0].value as String).get()
                                if (group == null) {
                                    sender.sendMessage("<red>The group <white>${args[0].value as String} <red>does not exist.")
                                    return@executes Result.SUCCESS
                                }
                                val parentName = args[1].value as String
                                val parentGroup = GroupRegistry.get(parentName).get()!!
                                if (parentGroup.name == group.name) {
                                    sender.sendMessage("<red>Cannot set a group as its own parent.")
                                    return@executes Result.SUCCESS
                                }
                                if (group.parents.contains(parentGroup.name)) {
                                    sender.sendMessage("<red>The group <white>${parentGroup.name}<red> is already a parent of <white>${group.name}<red>.")
                                    return@executes Result.SUCCESS
                                }

                                group.parents.add(parentGroup.name)
                                GroupRepository.insert(group)
                                sender.sendMessage("<green>Added parent '<white>${parentGroup.name}<green>' to '<white>${group.name}<green>'.")
                                Result.SUCCESS
                            } }
                        }
                        subcommand("remove") {
                            argument("parent", inputType = ArgumentInputType.STRING, dynamicProvider = Provider.GROUPS) { executes { sender, args ->
                                val group: Group? = GroupRegistry.get(args[0].value as String).get()
                                if (group == null) {
                                    sender.sendMessage("<red>The group <white>${args[0].value as String} <red>does not exist.")
                                    return@executes Result.SUCCESS
                                }
                                val parentName = args[1].value as String
                                if (!group.parents.contains(parentName)) {
                                    sender.sendMessage("<red>The group <white>$parentName <red>is not a parent of <white>${group.name}<red>.")
                                    return@executes Result.SUCCESS
                                }

                                group.parents.remove(parentName)
                                GroupRepository.insert(group)
                                sender.sendMessage("<red>Removed parent '<white>$parentName<red>' from group '<white>${group.name}<red>'.")
                                Result.SUCCESS
                            } }
                        }
                    }
                    subcommand("set") {
                        subcommand("display") {
                            argument("display", inputType = ArgumentInputType.STRING) { executes { sender, args ->
                                val group: Group? = GroupRegistry.get(args[0].value as String).get()
                                if (group == null) {
                                    sender.sendMessage("<red>The group <white>${args[0].value as String} <red>does not exist.")
                                    return@executes Result.SUCCESS
                                }

                                group.displayName = args[1].value as String
                                GroupRepository.insert(group)
                                sender.sendMessage("<green>Set the display name of '<white>${group.name}<green>' to '<white>${args[1].value as String}<green>'.")
                                Result.SUCCESS
                            } }
                        }
                        subcommand("weight") {
                            argument("weight", inputType = ArgumentInputType.INTEGER) { executes { sender, args ->
                                val group: Group? = GroupRegistry.get(args[0].value as String).get()
                                if (group == null) {
                                    sender.sendMessage("<red>The group <white>${args[0].value as String} <red>does not exist.")
                                    return@executes Result.SUCCESS
                                }

                                group.weight = args[1].value as Int
                                GroupRepository.insert(group)
                                sender.sendMessage("<green>Set the weight of '<white>${group.name}<green>' to '<white>${args[1].value as Int}<green>'.")
                                Result.SUCCESS
                            } }
                        }
                        subcommand("prefix") {
                            argument("prefix", inputType = ArgumentInputType.STRING) { executes { sender, args ->
                                val group: Group? = GroupRegistry.get(args[0].value as String).get()
                                if (group == null) {
                                    sender.sendMessage("<red>The group <white>${args[0].value as String} <red>does not exist.")
                                    return@executes Result.SUCCESS
                                }

                                group.prefix = args[1].value as String
                                GroupRepository.insert(group)
                                sender.sendMessage("<green>Set the prefix of '<white>${group.name}<green>' to '<white>${args[1].value as String}<green>'.")
                                Result.SUCCESS
                            } }
                        }
                        subcommand("suffix") {
                            argument("suffix", inputType = ArgumentInputType.STRING) { executes { sender, args ->
                                val group: Group? = GroupRegistry.get(args[0].value as String).get()
                                if (group == null) {
                                    sender.sendMessage("<red>The group <white>${args[0].value as String} <red>does not exist.")
                                    return@executes Result.SUCCESS
                                }

                                group.suffix = args[1].value as String
                                GroupRepository.insert(group)
                                sender.sendMessage("<green>Set the suffix of '<white>${group.name}<green>' to '<white>${args[1].value as String}<green>'.")
                                Result.SUCCESS
                            } }
                        }
                    }
                    subcommand("reset") {
                        subcommand("display") { executes { sender, args ->
                            val group: Group? = GroupRegistry.get(args[0].value as String).get()
                            if (group == null) {
                                sender.sendMessage("<red>The group <white>${args[0].value as String} <red>does not exist.")
                                return@executes Result.SUCCESS
                            }

                            group.displayName = ""
                            GroupRepository.insert(group)
                            sender.sendMessage("<green>Reset the display name of '<white>${group.name}<green>'.")
                            Result.SUCCESS
                        } }
                        subcommand("weight") { executes { sender, args ->
                            val group: Group? = GroupRegistry.get(args[0].value as String).get()
                            if (group == null) {
                                sender.sendMessage("<red>The group <white>${args[0].value as String} <red>does not exist.")
                                return@executes Result.SUCCESS
                            }

                            group.weight = 0
                            GroupRepository.insert(group)
                            sender.sendMessage("<green>Reset the weight of '<white>${group.name}<green>'.")
                            Result.SUCCESS
                        } }
                        subcommand("prefix") { executes { sender, args ->
                            val group: Group? = GroupRegistry.get(args[0].value as String).get()
                            if (group == null) {
                                sender.sendMessage("<red>The group <white>${args[0].value as String} <red>does not exist.")
                                return@executes Result.SUCCESS
                            }

                            group.prefix = ""
                            GroupRepository.insert(group)
                            sender.sendMessage("<green>Reset the prefix of '<white>${group.name}<green>'.")
                            Result.SUCCESS
                        } }
                        subcommand("suffix") { executes { sender, args ->
                            val group: Group? = GroupRegistry.get(args[0].value as String).get()
                            if (group == null) {
                                sender.sendMessage("<red>The group <white>${args[0].value as String} <red>does not exist.")
                                return@executes Result.SUCCESS
                            }

                            group.suffix = ""
                            GroupRepository.insert(group)
                            sender.sendMessage("<green>Reset the suffix of '<white>${group.name}<green>'.")
                            Result.SUCCESS
                        } }
                    }
                }
            }

            subcommand("user") {
                argument(Arguments.PLAYERS_REQUIRED) {
                    subcommand("info") { executes { sender, args ->
                        val target: Player = args[0].value as Player
                        sender.sendMessage(msg {
                            +"<green>Player: <white>${target.data.name}\n"
                            +"<yellow>| <green>UUID: <white>${target.data.uuid}\n"

                            +"<yellow>| <green>Permissions:\n"
                            val perms = target.getPermissions()
                            if (perms.isEmpty()) +"<yellow>| <green>- <gray>None\n"
                            else perms.forEach { +"<yellow>| <green>- <white>${it.node} <gray>[${if (it.value) "true" else "false"}${it.expiresAt?.let { t -> ", expires at $t" } ?: ""}]\n" }

                            +"<yellow><b>|</b></yellow> <green>Groups:\n"
                            val groups = target.getGroups()
                            if (groups.isEmpty()) +"<yellow>| <green>- <gray>None\n"
                            else groups.forEach { +"<yellow>| <green>- <white>${it.name} <gray>[${if (it.isExpired()) "expired" else "active"}]\n" }
                        })
                        Result.SUCCESS
                    } }
                    subcommand("permission") {
                        subcommand("add") {
                            argument("permission", inputType = ArgumentInputType.STRING) { executes { sender, args ->
                                val target: Player = args[0].value as Player
                                target.addPermission(Permission(args[1].value as String))
                                sender.sendMessage("<green>Added permission '<white>${args[1].value as String}<green>' to '<white>${target.data.name}<green>'.")
                                Result.SUCCESS
                            } }
                        }
                        subcommand("remove") {
                            argument("permission", inputType = ArgumentInputType.STRING) { executes { sender, args ->
                                val target: Player = args[0].value as Player
                                target.removePermission(
                                    Permission(args[2].value as String),
                                    PermissionScope.GLOBAL
                                )
                                sender.sendMessage("<red>Removed permission '<white>${args[2].value as String}<red>' from '<white>${target.data.name}<red>'.")
                                Result.SUCCESS
                            } }
                        }
                    }
                    subcommand("group") {
                        subcommand("add") {
                            argument("group", inputType = ArgumentInputType.STRING, dynamicProvider = Provider.GROUPS) { executes { sender, args ->
                                val target: Player = args[0].value as Player
                                val groupName = args[1].value as String
                                val group: Group? = GroupRegistry.get(groupName).get()
                                if (group == null) {
                                    sender.sendMessage("<red>The group <white>$groupName <red>does not exist.")
                                    return@executes Result.SUCCESS
                                }

                                target.addGroup(group)
                                sender.sendMessage("<green>Added group '<white>${group.name}<green>' to '<white>${target.data.name}<green>'.")
                                Result.SUCCESS
                            } }
                        }
                        subcommand("remove") {
                            argument("group", inputType = ArgumentInputType.STRING, dynamicProvider = Provider.GROUPS) { executes { sender, args ->
                                val target: Player = args[0].value as Player
                                val groupName = args[1].value as String
                                val group: Group? = GroupRegistry.get(groupName).get()
                                if (group == null) {
                                    sender.sendMessage("<red>The group <white>$groupName <red>does not exist.")
                                    return@executes Result.SUCCESS
                                }

                                target.removeGroup(group)
                                sender.sendMessage("<red>Removed group '<white>${group.name}<red>' from '<white>${target.data.name}<red>'.")
                                Result.SUCCESS
                            } }
                        }
                    }
                }
            }
        }
    }
}