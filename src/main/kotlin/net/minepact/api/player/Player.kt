package net.minepact.api.player

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.title.Title
import net.minecraft.server.level.ServerPlayer
import net.minepact.Main
import net.minepact.api.data.repository.LogRepository
import net.minepact.api.economy.EconomyHolder
import net.minepact.api.logging.LogInfo
import net.minepact.api.math.helper.vector.vec
import net.minepact.api.messages.FormatParser
import net.minepact.api.messages.Message
import net.minepact.api.permissions.Group
import net.minepact.api.permissions.Permissable
import net.minepact.api.permissions.Permission
import net.minepact.api.permissions.PermissionCache
import net.minepact.api.permissions.PermissionManager
import net.minepact.api.permissions.PermissionPersistence
import net.minepact.api.permissions.PermissionScope
import net.minepact.api.permissions.PlayerGroupData
import net.minepact.api.permissions.PlayerPermissionData
import net.minepact.api.world.Position
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.entity.CraftPlayer
import java.io.File
import org.bukkit.entity.Player as BukkitPlayer
import java.time.Duration
import java.time.Instant
import java.util.*

class Player(
    val data: PlayerData,
    val globalGroupData: PlayerGroupData,
    val localGroupData: PlayerGroupData,
    val globalPermissionData: PlayerPermissionData,
    val localPermissionData: PlayerPermissionData,
    // val economyData: EconomyData,
    var pos: Position,
    var online: Boolean,
    var vanished: Boolean
) : Permissable, EconomyHolder {
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
            globalGroupData = PlayerGroupData(groups = mutableListOf()),
            globalPermissionData = PlayerPermissionData(perms = mutableSetOf()),
            localGroupData = PlayerGroupData(groups = mutableListOf()),
            localPermissionData = PlayerPermissionData(perms = mutableSetOf()),
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

    /* ------------------------------------- FILE ------------------------------------- */

    fun dataFolder(): File {
        val file = File(Main.instance.dataFolder, "data/players/${data.uuid}")
        if (!file.exists()) file.mkdirs()

        return file
    }
    fun logFile(): File {
        val file = File(this.dataFolder(), "log.txt")
        if (!file.exists()) file.createNewFile()

        return file
    }
    fun clearLog() {
        val file = logFile()
        if (file.exists()) file.writeText("")
    }
    fun writeLog(log: LogInfo): File {
        val file = logFile()
        file.appendText(log.toString() + System.lineSeparator())
        return file
    }
    fun currentLogs(): List<LogInfo> {
        val file = logFile()
        if (!file.exists()) return emptyList()

        return file.readLines().map { LogInfo.fromString(it) }
    }
    fun insertCurrentLogs() {
        currentLogs().forEach { LogRepository.insertValues(it) }
        clearLog()
    }

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

    /* ------------------------------------- ECONOMY ------------------------------------- */
    /*
    override fun get(currency: Currency): Double {

    }
    override fun set(currency: Currency, amount: Double) {

    }

    override fun give(currency: Currency, amount: Double) {

    }
    override fun take(currency: Currency, amount: Double) {

    }
    override fun clear(currency: Currency) {

    }

    */
    /* ------------------------------------- PERMISSIONS ------------------------------------- */

    override fun getPermissions(): Set<Permission> {
        return localPermissionData.perms + globalPermissionData.perms
    }
    override fun hasPermission(permission: Permission): Boolean = PermissionManager.hasPermission(this, permission.node)
    override fun addPermission(permission: Permission) {
        localPermissionData.perms.add(permission)
        PermissionCache.invalidate(data.uuid)
        PermissionPersistence.markDirty(this)
    }
    override fun addTemporaryPermission(permission: Permission, duration: Duration) {
        addTemporaryPermission(permission, duration, PermissionScope.LOCAL)
    }
    override fun removePermission(permission: Permission) {
        removePermission(permission.node, PermissionScope.LOCAL)
    }

    fun getPermissions(scope: PermissionScope = PermissionScope.ALL): Set<Permission> {
        return when (scope) {
            PermissionScope.ALL -> localPermissionData.perms + globalPermissionData.perms
            PermissionScope.GLOBAL -> globalPermissionData.perms
            PermissionScope.LOCAL -> localPermissionData.perms
        }
    }
    fun hasPermission(node: String): Boolean = PermissionManager.hasPermission(this, node)
    fun addPermission(node: String) {
        addPermission(Permission(node))
    }
    fun addTemporaryPermission(node: String, duration: Duration) {
        addTemporaryPermission(Permission(node), duration, PermissionScope.LOCAL)
    }
    fun removePermission(node: String) {
        removePermission(node, PermissionScope.LOCAL)
    }

    fun hasPermission(permission: Permission, scope: PermissionScope = PermissionScope.ALL): Boolean {
        return when (scope) {
            PermissionScope.ALL -> hasPermission(permission)
            PermissionScope.GLOBAL -> globalPermissionData.perms.any { it.node == permission.node }
            PermissionScope.LOCAL -> localPermissionData.perms.any { it.node == permission.node }
        }
    }
    fun addPermission(permission: Permission, scope: PermissionScope = PermissionScope.LOCAL) {
        when (scope) {
            PermissionScope.LOCAL -> localPermissionData.perms.add(permission)
            PermissionScope.GLOBAL, PermissionScope.ALL -> globalPermissionData.perms.add(permission)
        }
        PermissionCache.invalidate(data.uuid)
        PermissionPersistence.markDirty(this)
    }
    fun addTemporaryPermission(permission: Permission, duration: Duration, scope: PermissionScope = PermissionScope.LOCAL) {
        val temp = permission.copy(expiresAt = Instant.now().plus(duration))

        when (scope) {
            PermissionScope.LOCAL -> localPermissionData.perms.add(temp)
            PermissionScope.GLOBAL, PermissionScope.ALL -> globalPermissionData.perms.add(temp)
        }

        PermissionCache.invalidate(data.uuid)
        PermissionPersistence.markDirty(this)
    }
    fun removePermission(permission: Permission, scope: PermissionScope = PermissionScope.ALL) {
        removePermission(permission.node, scope)
    }

    fun hasPermission(node: String, scope: PermissionScope = PermissionScope.ALL): Boolean {
        return when (scope) {
            PermissionScope.ALL -> hasPermission(node)
            PermissionScope.GLOBAL -> globalPermissionData.perms.any { it.node == node }
            PermissionScope.LOCAL -> localPermissionData.perms.any { it.node == node }
        }
    }
    fun addPermission(node: String, scope: PermissionScope = PermissionScope.LOCAL) {
        when (scope) {
            PermissionScope.LOCAL -> localPermissionData.perms.add(Permission(node))
            PermissionScope.GLOBAL, PermissionScope.ALL -> globalPermissionData.perms.add(Permission(node))
        }
        PermissionCache.invalidate(data.uuid)
        PermissionPersistence.markDirty(this)
    }
    fun addTemporaryPermission(node: String, duration: Duration, scope: PermissionScope = PermissionScope.LOCAL) {
        val temp = Permission(node).copy(expiresAt = Instant.now().plus(duration))

        when (scope) {
            PermissionScope.LOCAL -> localPermissionData.perms.add(temp)
            PermissionScope.GLOBAL, PermissionScope.ALL -> globalPermissionData.perms.add(temp)
        }

        PermissionCache.invalidate(data.uuid)
        PermissionPersistence.markDirty(this)
    }
    fun removePermission(node: String, scope: PermissionScope = PermissionScope.ALL) {
        when (scope) {
            PermissionScope.LOCAL -> localPermissionData.perms.removeIf { it.node == node }
            PermissionScope.GLOBAL -> globalPermissionData.perms.removeIf { it.node == node }
            PermissionScope.ALL -> {
                localPermissionData.perms.removeIf { it.node == node }
                globalPermissionData.perms.removeIf { it.node == node }
            }
        }

        PermissionCache.invalidate(data.uuid)
        PermissionPersistence.markDirty(this)
    }

    fun getPrimaryGroup(): Group? {
        return (localGroupData.groups + globalGroupData.groups).maxByOrNull { it.weight }
    }
    override fun addGroup(group: Group) {
        localGroupData.groups.add(group)
        PermissionCache.invalidate(data.uuid)
        PermissionPersistence.markDirty(this)
    }
    override fun removeGroup(group: Group) {
        removeGroup(group, PermissionScope.LOCAL)
    }
    override fun getGroups(): Set<Group> {
        return localGroupData.groups.toSet() + globalGroupData.groups.toSet()
    }
    override fun hasGroup(group: Group): Boolean {
        return localGroupData.groups.contains(group) || globalGroupData.groups.contains(group)
    }

    fun getPrimaryGroup(scope: PermissionScope = PermissionScope.ALL): Group? {
        return when (scope) {
            PermissionScope.ALL -> (localGroupData.groups + globalGroupData.groups).maxByOrNull { it.weight }
            PermissionScope.GLOBAL -> globalGroupData.groups.maxByOrNull { it.weight }
            PermissionScope.LOCAL -> localGroupData.groups.maxByOrNull { it.weight }
        }
    }
    fun addGroup(group: Group, scope: PermissionScope = PermissionScope.LOCAL) {
        when (scope) {
            PermissionScope.LOCAL -> localGroupData.groups.add(group)
            PermissionScope.GLOBAL, PermissionScope.ALL -> globalGroupData.groups.add(group)
        }

        PermissionCache.invalidate(data.uuid)
        PermissionPersistence.markDirty(this)
    }
    fun removeGroup(group: Group, scope: PermissionScope = PermissionScope.ALL) {
        when (scope) {
            PermissionScope.LOCAL -> localGroupData.groups.remove(group)
            PermissionScope.GLOBAL -> globalGroupData.groups.remove(group)
            PermissionScope.ALL -> {
                localGroupData.groups.remove(group)
                globalGroupData.groups.remove(group)
            }
        }

        PermissionCache.invalidate(data.uuid)
        PermissionPersistence.markDirty(this)
    }
    fun getGroups(scope: PermissionScope = PermissionScope.ALL): Set<Group> {
        return when (scope) {
            PermissionScope.ALL -> localGroupData.groups.toSet() + globalGroupData.groups.toSet()
            PermissionScope.GLOBAL -> globalGroupData.groups.toSet()
            PermissionScope.LOCAL -> localGroupData.groups.toSet()
        }
    }
    fun hasGroup(group: Group, scope: PermissionScope = PermissionScope.ALL): Boolean {
        return when (scope) {
            PermissionScope.ALL ->
                localGroupData.groups.contains(group) || globalGroupData.groups.contains(group)

            PermissionScope.GLOBAL ->
                globalGroupData.groups.contains(group)

            PermissionScope.LOCAL ->
                localGroupData.groups.contains(group)
        }
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