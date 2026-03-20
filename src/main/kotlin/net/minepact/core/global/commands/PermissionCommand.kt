package net.minepact.core.global.commands

import net.minepact.api.command.CommandUsage
import net.minepact.api.command.Result
import net.minepact.api.command.arguments.Argument
import net.minepact.api.command.arguments.ExpectedArgument
import net.minepact.api.data.repository.GroupRepository
import net.minepact.api.permissions.Group
import net.minepact.api.permissions.GroupRegistry
import net.minepact.api.player.Player
import net.minepact.api.permissions.Permission
import net.minepact.api.player.PlayerRegistry
import kotlin.collections.forEach
import kotlin.collections.getOrNull
import kotlin.let
import kotlin.text.lowercase
import kotlin.text.toInt
import net.minepact.api.command.Command

class PermissionCommand : Command(
    name = "permission",
    description = "Manage permissions for players and groups.",
    permission = Permission("minepact.permission.manage"),
    usage = CommandUsage(label = "permission", arguments = listOf(
        ExpectedArgument("action", listOf("group", "user"))
    )
    ),
    playerOnly = false,
    aliases = mutableListOf("perm", "perms", "pm", "permission-manager")
) {
    val PREFIX: String = "<hex:2bc5fb><bold>PERMS</bold></hex> <grey>»</grey> <reset>"

    override fun execute(
        sender: Player, args: MutableList<Argument<*>>
    ): Result {
        val action: String = args[0].value as String

        when (action.lowercase()) {
            "group" -> return groupCommand(sender, args)
            "user" -> return userCommand(sender, args)
        }
        return Result.SUCCESS
    }

    private fun groupCommand(sender: Player, args: MutableList<Argument<*>>): Result {
        // /perm group create <name>
        // /perm group delete <name>
        // /perm group info <name>
        // /perm group modify <group> perm add <permission> [true|false] [duration]
        // /perm group modify <group> perm rem <permission> [true|false] [duration]
        // /perm group modify <group> perm edit <permission> [true|false] [duration]
        // /perm group modify <group> parent add <group>
        // /perm group modify <group> parent rem <group>
        // /perm group modify <group> set display <new_name>
        // /perm group modify <group> set weight <new_name>
        // /perm group modify <group> set prefix <new_name>
        // /perm group modify <group> set suffix <suffix>

        val name = args[2].value as String
        when (args[1].value as String) {
            "create" -> {
                val group = Group(
                    name = name,
                    displayName = name,
                    weight = 0
                )

                GroupRegistry.register(group)
                GroupRepository.insert(group)

                sender.sendMessage("$PREFIX<green>Group '<white>$name<green>' created.")
            }
            "delete" -> {
                val group = GroupRegistry.get(name).get()!!

                GroupRegistry.unregister(group)
                GroupRepository.delete(group)

                sender.sendMessage("$PREFIX<red>Group '<white>$name<red>' deleted.")
            }
            "info" -> {
                val group = GroupRegistry.get(name).get()!!

                sender.sendMessage("<strikethrough><red>--------------------------</red></strikethrough>")
                sender.sendMessage("<green>Group: <white>${group.name}")
                sender.sendMessage("<yellow><b>|</b></yellow> <green>Display Name: <white>${group.displayName ?: "None"}")
                sender.sendMessage("<yellow><b>|</b></yellow> <green>Weight: <white>${group.weight}")
                sender.sendMessage("<yellow><b>|</b></yellow> <green>Prefix: <white>${group.prefix ?: "None"}")
                sender.sendMessage("<yellow><b>|</b></yellow> <green>Suffix: <white>${group.suffix ?: "None"}")
                sender.sendMessage("<yellow><b>|</b></yellow> <green>Permissions:")
                if (group.permissions.isEmpty()) sender.sendMessage("<yellow><b>|</b></yellow> <green>- <gray>None")
                else group.permissions.forEach { sender.sendMessage("<yellow><b>|</b></yellow> <green>- <white>${it.node} <gray>[${if (it.value) "true" else "false"}${it.expiresAt?.let { t -> ", expires at $t" } ?: ""}]") }
                sender.sendMessage("<yellow><b>|</b></yellow> <green>Parents:")
                if (group.parents.isEmpty()) sender.sendMessage("<yellow><b>|</b></yellow> <green>- <gray>None")
                else group.parents.forEach { sender.sendMessage("<yellow><b>|</b></yellow> <green>- <white>$it") }
                sender.sendMessage("<yellow><b>|</b></yellow> <green>Expires: <white>${group.expiresAt ?: "Never"}")
                sender.sendMessage("<strikethrough><red>--------------------------</red></strikethrough>")
            }
            "modify" -> {
                val group = GroupRegistry.get(name).get()!!
                when (args[3].value as String) {
                    "perm" -> handleGroupPermission(group, sender, args)
                    "set" -> handleGroupMeta(group, sender, args)
                    "parent" -> {
                        val action = args[4].value as String
                        val parentName = args[5].value as String
                        val parentGroup = GroupRegistry.get(parentName).get()!!

                        when (action) {
                            "add" -> {
                                if (parentGroup.name == group.name) {
                                    sender.sendMessage("$PREFIX<red>Cannot set a group as its own parent.")
                                    return Result.FAILURE
                                }
                                if (group.parents.contains(parentGroup.name)) {
                                    sender.sendMessage("$PREFIX<red>Group '<white>${parentGroup.name}<red>' is already a parent of '<white>${group.name}<red>'.")
                                    return Result.FAILURE
                                }
                                group.parents.add(parentGroup.name)
                                sender.sendMessage("$PREFIX<green>Added parent group '<white>${parentGroup.name}<green>' to group '<white>${group.name}<green>'.")
                            }
                            "rem" -> {
                                if (!group.parents.contains(parentGroup.name)) {
                                    sender.sendMessage("$PREFIX<red>Group '<white>${parentGroup.name}<red>' is not a parent of '<white>${group.name}<red>'.")
                                    return Result.FAILURE
                                }
                                group.parents.remove(parentGroup.name)
                                sender.sendMessage("$PREFIX<red>Removed parent group '<white>${parentGroup.name}<red>' from group '<white>${group.name}<red>'.")
                            }
                        }
                    }
                }
            }
        }
        return Result.SUCCESS
    }
    private fun handleGroupPermission(
        group: Group,
        sender: Player,
        args: MutableList<Argument<*>>
    ) {
        val action = args[4].value as String
        val permNode = args[5].value as String
        val value = args.getOrNull(6)?.value as? String ?: "true"

        when (action) {
            "add" -> {
                group.permissions.add(Permission(permNode, (value == "true")))
                sender.sendMessage("$PREFIX<green>Added permission '<white>$permNode<green>' to group '<white>${group.name}<green>'.")
            }
            "rem" -> {
                group.permissions.removeIf { it.node == permNode }
                sender.sendMessage("$PREFIX<red>Removed permission '<white>$permNode<red>' from group '<white>${group.name}<red>'.")
            }
            "edit" -> {
                group.permissions.removeIf { it.node == permNode }
                group.permissions.add(Permission(permNode, (value == "true")))
                sender.sendMessage("$PREFIX<green>Updated permission '<white>$permNode<green>' for group '<white>${group.name}<green>'.")
            }
        }
        GroupRepository.insert(group)
    }
    private fun handleGroupMeta(
        group: Group,
        sender: Player,
        args: MutableList<Argument<*>>
    ) {
        val key = args[4].value as String
        val value = args[5].value as String

        when (key) {
            "display" -> {
                group.displayName = value
                sender.sendMessage("$PREFIX<green>Set <white>${group.name}</white>'s display name to '<white>$value<green>'.")
            }
            "weight" -> {
                group.weight = value.toInt()
                sender.sendMessage("$PREFIX<green>Set <white>${group.name}</white>'s weight to '<white>$value<green>'.")
            }
            "prefix" -> {
                group.prefix = value
                sender.sendMessage("$PREFIX<green>Set <white>${group.name}</white>'s prefix to '<white>$value<green>'.")
            }
            "suffix" -> {
                group.suffix = value
                sender.sendMessage("$PREFIX<green>Set <white>${group.name}</white>'s suffix name to '<white>$value<green>'.")
            }
        }

        GroupRegistry.unregister(group)
        GroupRegistry.register(group)
        GroupRepository.insert(group)
    }

    private fun userCommand(sender: Player, args: MutableList<Argument<*>>): Result {
        // /perm user <player> perm add <permission> [true|false] [duration]
        // /perm user <player> perm rem <permission> [true|false] [duration]
        // /perm user <player> perm edit <permission> [true|false] [duration]
        // /perm user <player> group add <group> [duration]
        // /perm user <player> group rem <group> [duration]
        // /perm user <player> info

        val playerName = args[1].value as String
        val target = PlayerRegistry.get(playerName).get()
        val type = args[2].value as String
        when (type) {
            "perm" -> {
                val action = args[3].value as String
                val node = args[4].value as String
                val value = args.getOrNull(5)?.value as? String ?: "true"

                when (action) {
                    "add" -> {
                        target.addPermission(Permission(node, true))
                        sender.sendMessage("$PREFIX<green>Added permission '<white>$node<green>' to player '<white>${target.data.name}<green>'.")
                    }
                    "rem" -> {
                        target.removePermission(Permission(node, true))
                        sender.sendMessage("$PREFIX<red>Removed permission '<white>$node<red>' from player '<white>${target.data.name}<red>'.")
                    }
                    "edit" -> {
                        target.removePermission(Permission(node, true))
                        target.addPermission(Permission(node, (value == "true")))
                        sender.sendMessage("$PREFIX<green>Updated permission '<white>$node<green>' for player '<white>${target.data.name}<green>'.")
                    }
                }
            }
            "group" -> {
                val action = args[3].value as String
                val groupName = args[4].value as String
                val group = GroupRegistry.get(groupName).get()!!

                when (action) {
                    "add" -> {
                        target.addGroup(group)
                        sender.sendMessage("$PREFIX<green>Added group '<white>${group.name}<green>' to player '<white>${target.data.name}<green>'.")
                    }
                    "rem" -> {
                        target.removeGroup(group)
                        sender.sendMessage("$PREFIX<red>Removed group '<white>${group.name}<red>' from player '<white>${target.data.name}<red>'.")
                    }
                }
            }
            "info" -> {
                    sender.sendMessage("<strikethrough><red>--------------------------</red></strikethrough>")
                    sender.sendMessage("<green>Player: <white>${target.data.name}")
                    sender.sendMessage("<yellow><b>|</b></yellow> <green>UUID: <white>${target.data.uuid}")
                    sender.sendMessage("<yellow><b>|</b></yellow> <green>Permissions:")
                    val perms = target.getPermissions()
                    if (perms.isEmpty()) sender.sendMessage("<yellow><b>|</b></yellow> <green>- <gray>None")
                    else perms.forEach { sender.sendMessage("<yellow><b>|</b></yellow> <green>- <white>${it.node} <gray>[${if (it.value) "true" else "false"}${it.expiresAt?.let { t -> ", expires at $t" } ?: ""}]") }
                    sender.sendMessage("<yellow><b>|</b></yellow> <green>Groups:")
                    val groups = target.getGroups()
                    if (groups.isEmpty()) sender.sendMessage("<yellow><b>|</b></yellow> <green>- <gray>None")
                    else groups.forEach { sender.sendMessage("<yellow><b>|</b></yellow> <green>- <white>${it.name} <gray>[${if (it.isExpired()) "expired" else "active"}]") }
                    sender.sendMessage("<strikethrough><red>--------------------------</red></strikethrough>")
            }
        }

        return Result.SUCCESS
    }
}