package net.minepact.api.command

import net.minepact.api.command.arguments.Argument
import net.minepact.api.messages.helper.formatString
import net.minepact.api.player.Player
import net.minepact.api.permissions.Permission
import net.minepact.api.server.ServerType

/**
 * The base command which gives the structure for every command.
 *
 * @property server The server which the command will be used on. Default [ServerType.GLOBAL].
 * @property name The name of the command, which is used in the label.
 * @property description A brief description of the command, which is used in /help.
 * @property permission The permission node required to execute the command.
 * @property usage How the command is used which is used in /help and errors. [CommandUsage]
 * @property aliases Alternative labels for the command.
 * @property cooldown The cooldown of the command in seconds. Default -1.
 * @property playerOnly Whether the command can only be executed by players. Default false.
 * @property maxArgs The maximum amount of args the command can take. Default [Int.MAX_VALUE].
 * @property log Whether the command execution should be logged. Default false.
 *
 * @author dankenyon - 22/02/26
 */
abstract class Command(
    val server: ServerType = ServerType.GLOBAL,
    val name: String,
    val description: String,
    val permission: Permission,
    var usage: CommandUsage,
    var aliases: MutableList<String> = mutableListOf(),
    var cooldown: Double = -1.0,
    val playerOnly: Boolean = false,
    val maxArgs: Int = Int.MAX_VALUE,
    val log: Boolean = false,
) {
    abstract fun execute(sender: Player, args: MutableList<Argument<*>>): Result

    override fun toString(): String = formatString(
        "Command[",
        "\tname=$name, ",
        "\tdescription=$description, ",
        "\tpermission=$permission, ",
        "\tusage=$usage, ",
        "\taliases=${aliases.joinToString(", ")}, ",
        "\tcooldown=$cooldown, ",
        "\tplayerOnly=$playerOnly, ",
        "\tmaxArgs=$maxArgs, ",
        "]"
    )
    override fun equals(other: Any?): Boolean {
        return (this.name == (other as Command).name && this.javaClass.simpleName == other.javaClass.simpleName)
    }
    override fun hashCode(): Int {
        var result = cooldown.hashCode()
        result = 31 * result + playerOnly.hashCode()
        result = 31 * result + maxArgs
        result = 31 * result + name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + permission.hashCode()
        result = 31 * result + usage.hashCode()
        result = 31 * result + aliases.hashCode()
        return result
    }
}
