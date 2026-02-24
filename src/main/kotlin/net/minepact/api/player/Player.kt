package net.minepact.api.player

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.title.Title
import net.luckperms.api.node.types.InheritanceNode
import net.luckperms.api.node.types.PermissionNode
import net.minepact.Main
import net.minepact.api.messages.FormatParser
import net.minepact.api.messages.Message
import net.minepact.api.player.permissions.Group
import net.minepact.api.player.permissions.Permissable
import net.minepact.api.player.permissions.Permission
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player as BukkitPlayer
import java.time.Duration
import java.util.*

class Player(
    val data: PlayerData,
    var online: Boolean
) : Permissable {
    companion object {
        val CONSOLE: Player = Player(
            PlayerData(
                uuid = UUID(0, 0),
                name = "CONSOLE",
                ipHistory = listOf("127.0.0.1"),
                discordId = "",
                firstJoined = 0L,
                lastSeen = 0L
            ),
            online = true
        )
    }

    fun asCommandSender(): CommandSender {
        if (console()) return Bukkit.getConsoleSender()
        val p: BukkitPlayer? = Bukkit.getPlayer(data.uuid)
        if (p != null && p.isOnline) return p
        return Bukkit.getOfflinePlayer(data.uuid) as CommandSender
    }
    fun asOfflinePlayer(): OfflinePlayer? {
        if (console()) return null
        return Bukkit.getOfflinePlayer(data.uuid)
    }
    fun asPlayer(): BukkitPlayer? {
        if (console()) return null

        val p: BukkitPlayer? = Bukkit.getPlayer(data.uuid)
        if (p != null && p.isOnline) return p
        return null
    }

    fun sendMessage(message: Message) {
        if (!online) return
        val player = Bukkit.getPlayer(data.uuid)

        if (player != null && player.isOnline) player.sendMessage(message.toAdventureComponent())
        else if (console()) Bukkit.getConsoleSender().sendMessage(message.toAdventureComponent())
        else Main.instance.logger.info("[Message] Could not deliver to ${data.name}: ${message.components.joinToString(separator = "") { it.text }}")
    }
    fun sendMessage(message: String) {
        if (!online) return
        val player = Bukkit.getPlayer(data.uuid)

        if (player != null && player.isOnline) player.sendMessage(FormatParser.parse(message))
        else if (console()) Bukkit.getConsoleSender().sendMessage(FormatParser.parse(message))
        else Main.instance.logger.info("[Message] Could not deliver to ${data.name}: ${MiniMessage.miniMessage().deserialize(message)}")
    }
    fun sendMessage(message: Component) {
        if (!online) return
        val player = Bukkit.getPlayer(data.uuid)

        if (player != null && player.isOnline) player.sendMessage(message)
        else if (console()) Bukkit.getConsoleSender().sendMessage(message)
        else Main.instance.logger.info("[Message] Could not deliver to ${data.name}: $message")
    }

    fun title(s1: String, s2: String = "") {
        if (!online || console()) return

        val player = Bukkit.getPlayer(data.uuid)
        if (player != null && player.isOnline)
            player.showTitle(Title.title(
                MiniMessage.miniMessage().deserialize(s1),
                MiniMessage.miniMessage().deserialize(s2)
            ))
        else Main.instance.logger.info("[Title] Could not deliver to ${data.name}: ${MiniMessage.miniMessage().deserialize(s1)} | ${MiniMessage.miniMessage().deserialize(s2)}")
    }
    fun title(s1: Component, s2: Component = Component.text("")) {
        if (!online || console()) return

        val player = Bukkit.getPlayer(data.uuid)
        if (player != null && player.isOnline) player.showTitle(Title.title(s1, s2))
        else Main.instance.logger.info("[Title] Could not deliver to ${data.name}: $s1 | $s2")
    }
    fun actionBar(message: String) {
        if (!online || console()) return

        val player = Bukkit.getPlayer(data.uuid)

        if (player != null && player.isOnline) player.sendActionBar(MiniMessage.miniMessage().deserialize(message))
        else Main.instance.logger.info("[ActionBar] Could not deliver to ${data.name}: ${MiniMessage.miniMessage().deserialize(message)}")
    }
    fun actionBar(message: Component) {
        if (!online || console()) return

        val player = Bukkit.getPlayer(data.uuid)

        if (player != null && player.isOnline) player.sendActionBar(message)
        else Main.instance.logger.info("[ActionBar] Could not deliver to ${data.name}: $message")
    }

    fun console(): Boolean = data.uuid == CONSOLE.data.uuid

    override fun toString(): String {
        return """
            Player[
                data=$data, 
                online=$online
            ]
        """.trimMargin()
    }

    override fun getPermissions(): Set<Permission> {
        val user = Main.LUCKPERMS_API.userManager.getUser(data.uuid)
            ?: return emptySet()

        return user.nodes
            .filterIsInstance<PermissionNode>()
            .map { Permission(
                    node = it.permission,
                    expiresAt = it.expiry
            ) }.toSet()
    }
    override fun hasPermission(permission: Permission): Boolean {
        if (data.uuid == CONSOLE.data.uuid) return true

        val user = Main.LUCKPERMS_API.userManager.getUser(data.uuid)
            ?: return false

        return user.cachedData.permissionData
            .checkPermission(permission.node)
            .asBoolean()
    }
    override fun addPermission(permission: Permission) {
        val user = Main.LUCKPERMS_API.userManager.getUser(data.uuid)
            ?: return

        val node = PermissionNode
            .builder(permission.node)
            .build()

        user.data().add(node)
        Main.LUCKPERMS_API.userManager.saveUser(user)
    }
    override fun addTemporaryPermission(permission: Permission, duration: Duration) {
        val user = Main.LUCKPERMS_API.userManager.getUser(data.uuid) ?: return
        val node = PermissionNode.builder(permission.node)
            .expiry(duration)
            .build()

        user.data().add(node)
        Main.LUCKPERMS_API.userManager.saveUser(user)
    }
    override fun removePermission(permission: Permission) {
        val user = Main.LUCKPERMS_API.userManager.getUser(data.uuid)
            ?: return

        val node = PermissionNode
            .builder(permission.node)
            .build()

        user.data().remove(node)
        Main.LUCKPERMS_API.userManager.saveUser(user)
    }

    fun getPrimaryGroup(): Group? {
        val user = Main.LUCKPERMS_API.userManager.getUser(data.uuid)
            ?: return null

        val groupName = user.primaryGroup
        val group = Main.LUCKPERMS_API.groupManager.getGroup(groupName)
            ?: return null

        return Group(
            name = group.name,
            displayName = group.displayName,
            weight = group.weight.orElse(0),
            prefix = group.cachedData.metaData.prefix,
            suffix = group.cachedData.metaData.suffix
        )
    }
    override fun getGroups(): Set<Group> {
        val user = Main.LUCKPERMS_API.userManager.getUser(data.uuid)
            ?: return emptySet()

        return user.getInheritedGroups(user.queryOptions)
            .map {
                Group(
                    name = it.name,
                    displayName = it.displayName,
                    weight = it.weight.orElse(0),
                    prefix = it.cachedData.metaData.prefix,
                    suffix = it.cachedData.metaData.suffix
                )
            }
            .toSet()
    }
    override fun hasGroup(group: Group): Boolean {
        return getGroups().any { it.name.equals(group.name, true) }
    }
    override fun addGroup(group: Group) {
        val user = Main.LUCKPERMS_API.userManager.getUser(data.uuid)
            ?: return

        val node = InheritanceNode
            .builder(group.name)
            .build()

        user.data().add(node)
        Main.LUCKPERMS_API.userManager.saveUser(user)
    }
    override fun removeGroup(group: Group) {
        val user = Main.LUCKPERMS_API.userManager.getUser(data.uuid)
            ?: return

        val node = InheritanceNode
            .builder(group.name)
            .build()

        user.data().remove(node)
        Main.LUCKPERMS_API.userManager.saveUser(user)
    }
}