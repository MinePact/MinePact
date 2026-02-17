package net.minepact.api.command

import net.minepact.api.command.arguments.Argument
import net.minepact.api.messages.formatString
import net.minepact.api.server.ServerType
import org.bukkit.command.CommandSender

abstract class Command(
    val server: ServerType = ServerType.GLOBAL,
    val name: String,
    val description: String,
    val permission: String,
    var usage: CommandUsage,
    var aliases: MutableList<String> = mutableListOf(),
    var cooldown: Double = -1.0,
    val playerOnly: Boolean = false,
    val maxArgs: Int = Int.MAX_VALUE,
    val log: Boolean = false,
) {
    protected val subCommands: MutableMap<String, SubCommand> = mutableMapOf()

    fun getSubCommand(name: String?): SubCommand? = subCommands[name?.lowercase()]
    fun allSubCommands(): Collection<SubCommand> = subCommands.values
    fun registerSubCommand(sub: SubCommand) {
        subCommands[sub.name.lowercase()] = sub
    }

    abstract fun execute(sender: CommandSender, args: MutableList<Argument<*>>): Result

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
        "\tsubCommands=${subCommands.keys}",
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
        result = 31 * result + subCommands.hashCode()
        return result
    }
}
