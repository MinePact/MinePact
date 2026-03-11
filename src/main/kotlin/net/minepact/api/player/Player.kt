package net.minepact.api.player

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.title.Title
import net.minecraft.server.level.ServerPlayer
import net.minepact.Main
import net.minepact.api.math.helper.vector.vec
import net.minepact.api.messages.FormatParser
import net.minepact.api.messages.Message
import net.minepact.api.permissions.Group
import net.minepact.api.permissions.Permissable
import net.minepact.api.permissions.Permission
import net.minepact.api.permissions.PermissionCache
import net.minepact.api.permissions.PermissionManager
import net.minepact.api.permissions.PermissionPersistence
import net.minepact.api.permissions.PlayerGroupData
import net.minepact.api.permissions.PlayerPermissionData
import net.minepact.api.world.Position
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player as BukkitPlayer
import java.time.Duration
import java.time.Instant
import java.util.*

class Player(
    val data: PlayerData,
    val groupData: PlayerGroupData,
    val permissionData: PlayerPermissionData,
    var pos: Position,
    var online: Boolean,
    var vanished: Boolean
) : Permissable {
    companion object {
        val CONSOLE: Player = Player(
            data = PlayerData(
                uuid = UUID(0, 0),
                name = "CONSOLE",
                ipHistory = listOf("127.0.0.1"),
                discordId = "",
                firstJoined = 0L,
                lastSeen = 0L
            ),
            groupData = PlayerGroupData(
                groups = mutableListOf()
            ),
            permissionData = PlayerPermissionData(
                perms = mutableSetOf()
            ),
            pos = Position(
                vector = vec(0, 0, 0),
                yaw = 0f,
                pitch = 0f,
                world = "world"
            ),
            online = true,
            vanished = false
        )
    }

    /* ------------------------------------- CASTING ------------------------------------- */

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
    fun asCraftPlayer(): CraftPlayer {
        return (asPlayer() as CraftPlayer)
    }
    fun asNMSPlayer(): ServerPlayer {
        return asCraftPlayer().handle
    }

    /* ------------------------------------- MESSAGES ------------------------------------- */

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

    /* ------------------------------------- POSITION ------------------------------------- */

    fun teleport(position: Position) {
        if (!online || console()) return

        val player = Bukkit.getPlayer(data.uuid)
        if (player != null && player.isOnline) {
            player.teleport(pos.asBukkitLocation())
            pos = position
        } else Main.instance.logger.info("[Teleport] Could not teleport ${data.name} to ${position.vector} in world ${position.world}")
    }

    /* ------------------------------------- STAFF ------------------------------------- */

    fun toggleVanish() {
        if (!online) return

        vanished = !vanished
    }
    fun toggleStaffMode() {

    }

    fun hidePlayer(target: Player) {
        if (!online) return

        val player = Bukkit.getPlayer(data.uuid)
        val targetPlayer = Bukkit.getPlayer(target.data.uuid)

        if (player != null && player.isOnline && targetPlayer != null && targetPlayer.isOnline) {
            player.hidePlayer(Main.instance, targetPlayer)
        } else Main.instance.logger.info("[Vanish] Could not hide ${target.data.name} from ${data.name}")
    }

    /* ------------------------------------- PERMISSIONS ------------------------------------- */

    override fun getPermissions(): Set<Permission> {
        return permissionData.perms
    }
    override fun hasPermission(permission: Permission): Boolean = PermissionManager.hasPermission(this, permission.node)
    fun hasPermission(node: String): Boolean = PermissionManager.hasPermission(this, node)
    override fun addPermission(permission: Permission) {
        permissionData.perms.add(permission)
        PermissionCache.invalidate(data.uuid)
        PermissionPersistence.markDirty(this)
    }
    override fun addTemporaryPermission(permission: Permission, duration: Duration) {
        permissionData.perms.add(permission.copy(expiresAt = Instant.now().plus(duration)))
        PermissionCache.invalidate(data.uuid)
        PermissionPersistence.markDirty(this)
    }
    override fun removePermission(permission: Permission) {
        permissionData.perms.removeIf { it.node == permission.node }
        PermissionCache.invalidate(data.uuid)
        PermissionPersistence.markDirty(this)
    }

    fun getPrimaryGroup(): Group? {
        return groupData.groups.maxByOrNull { it.weight }
    }
    override fun addGroup(group: Group) {
        groupData.groups.add(group)
        PermissionCache.invalidate(data.uuid)
        PermissionPersistence.markDirty(this)
    }
    override fun removeGroup(group: Group) {
        groupData.groups.remove(group)
        PermissionCache.invalidate(data.uuid)
        PermissionPersistence.markDirty(this)
    }
    override fun getGroups(): Set<Group> {
        return groupData.groups.toSet()
    }
    override fun hasGroup(group: Group): Boolean {
        return groupData.groups.contains(group)
    }

    /* ------------------------------------- HELPER ------------------------------------- */

    override fun toString(): String {
        return """
            Player[
                data=$data, 
                online=$online
            ]
        """.trimMargin()
    }
    override fun equals(other: Any?): Boolean {
        return other is Player && data.uuid == other.data.uuid
    }
    override fun hashCode(): Int {
        var result = online.hashCode()
        result = 31 * result + data.hashCode()
        result = 31 * result + pos.hashCode()
        return result
    }
}