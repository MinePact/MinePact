package net.minepact.api.config.custom

import net.minepact.api.config.custom.exception.ConfigException
import net.minepact.api.config.custom.interfaces.FileParser
import net.minepact.api.config.custom.interfaces.FileReader
import net.minepact.api.config.custom.interfaces.FileWriter
import net.minepact.api.config.custom.type.minepact.MinePactFileParser
import net.minepact.api.config.custom.type.minepact.MinePactFileReader
import net.minepact.api.config.custom.type.minepact.MinePactFileWriter
import java.io.File

sealed class ConfigType {
    abstract val formatName: String
    abstract val fileExtension: String

    abstract fun createParser(): FileParser
    abstract fun createReader(data: MutableMap<String, ConfigValue>): FileReader
    abstract fun createWriter(data: MutableMap<String, ConfigValue>, file: File): FileWriter

    object MinePact : ConfigType() {
        override val formatName = "mpc"
        override val fileExtension = "mpc"
        override fun createParser() = MinePactFileParser()
        override fun createReader(data: MutableMap<String, ConfigValue>): FileReader = MinePactFileReader(data)
        override fun createWriter(data: MutableMap<String, ConfigValue>, file: File): FileWriter =
            MinePactFileWriter(data, file)
    }
    object Json : ConfigType() {
        override val formatName = "json"
        override val fileExtension = "json"
        override fun createParser() = notImplemented()
        override fun createReader(data: MutableMap<String, ConfigValue>): FileReader = notImplemented()
        override fun createWriter(data: MutableMap<String, ConfigValue>, file: File): FileWriter = notImplemented()
        private fun notImplemented(): Nothing = throw ConfigException("JSON config format is not yet implemented")
    }
    object Yaml : ConfigType() {
        override val formatName = "yaml"
        override val fileExtension = "yaml"
        override fun createParser() = notImplemented()
        override fun createReader(data: MutableMap<String, ConfigValue>): FileReader = notImplemented()
        override fun createWriter(data: MutableMap<String, ConfigValue>, file: File): FileWriter = notImplemented()
        private fun notImplemented(): Nothing = throw ConfigException("YAML config format is not yet implemented")
    }
}