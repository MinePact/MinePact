package net.minepact.api.scripts

import net.kyori.adventure.text.minimessage.MiniMessage
import net.minepact.api.scripts.annotations.DependsOn
import net.minepact.api.scripts.annotations.ScriptBase
import net.minepact.api.scripts.annotations.ScriptName
import kotlin.script.experimental.api.ScriptAcceptedLocation
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.acceptedLocations
import kotlin.script.experimental.api.baseClass
import kotlin.script.experimental.api.compilerOptions
import kotlin.script.experimental.api.defaultImports
import kotlin.script.experimental.api.ide
import kotlin.script.experimental.api.refineConfigurationOnAnnotations
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvm.updateClasspath
import kotlin.script.experimental.jvm.util.classpathFromClassloader

object MinePactScriptCompilationConfig : ScriptCompilationConfiguration({
    baseClass(MinePactScript::class)

    defaultImports(
        "org.bukkit.*",
        "org.bukkit.entity.*",
        "org.bukkit.event.*",
        "org.bukkit.event.player.*",
        "org.bukkit.event.entity.*",
        "org.bukkit.event.block.*",
        "org.bukkit.event.inventory.*",
        "org.bukkit.inventory.*",
        "org.bukkit.inventory.meta.*",
        "org.bukkit.potion.*",
        "org.bukkit.attribute.*",
        "org.bukkit.enchantments.*",
        "org.bukkit.scheduler.*",
        "org.bukkit.util.*",

        "io.papermc.paper.event.player.*",
        "io.papermc.paper.event.entity.*",

        "net.kyori.adventure.text.*",
        "net.kyori.adventure.text.minimessage.*",
        "net.kyori.adventure.sound.Sound",

        "net.minepact.api.*",
        "net.minepact.api.messages.*",
        "net.minepact.api.server.*",
        "net.minepact.api.command.*",
        "net.minepact.api.command.arguments.*",
        "net.minepact.api.event.*",
        "net.minepact.api.command.custom.*",
        "net.minepact.api.player.*",
        "net.minepact.api.player.permissions.*",
        "net.minepact.api.player.discord.*",
        "net.minepact.api.enchantment.*",
        "net.minepact.api.discord.*",
        "net.minepact.api.discord.embeds.*",
        "net.minepact.api.scripting.*",
        "net.minepact.api.scripting.annotations.*",
        "net.minepact.api.scripting.bootstrap.*",
        "net.minepact.api.scripting.exceptions.*",
        "net.minepact.api.scripting.registries.*",

        "kotlin.time.*",
        "kotlin.time.Duration.Companion.*"
    )

    jvm {
        val classLoader = MinePactScript::class.java.classLoader
        val classpath = classpathFromClassloader(classLoader)
            ?: error("[ScriptEngine] Could not resolve classpath from plugin classloader")
        updateClasspath(classpath)
    }
    ide {
        acceptedLocations(ScriptAcceptedLocation.Everywhere)
    }
    compilerOptions(
        "-jvm-target", "21",
        "-opt-in=kotlin.time.ExperimentalTime"
    )
}) {
    private fun readResolve(): Any = MinePactScriptCompilationConfig
}