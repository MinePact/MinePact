package net.minepact.api.config.custom.interfaces

import net.minepact.api.config.custom.ConfigValue
import kotlin.reflect.KType

interface FileReader {
    fun <T> get(path: String, type: KType): T

    fun contains(path: String): Boolean

    fun keys(): Set<String>
    fun allPaths(): Set<String>

    fun raw(path: String): ConfigValue?
}