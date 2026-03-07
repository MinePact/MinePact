package net.minepact.api.config.custom.interfaces

import net.minepact.api.config.custom.ConfigValue

interface FileParser {
    fun parse(content: String): Map<String, ConfigValue>
}