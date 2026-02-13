package net.minepact.api.command.scripts

import net.minepact.Main
import net.minepact.api.command.Command
import java.io.File

fun command(block: ScriptCommandBuilder.() -> Unit): Command {
    return ScriptCommandBuilder().apply(block).build()
}