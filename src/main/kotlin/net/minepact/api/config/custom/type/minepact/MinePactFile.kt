package net.minepact.api.config.custom.type.minepact

import net.minepact.api.config.custom.ConfigType
import net.minepact.api.config.custom.interfaces.FileReader
import net.minepact.api.config.custom.interfaces.FileWriter

class MinePactFile internal constructor(
    val path:   String,
    val type: ConfigType,
    val reader: FileReader,
    val writer: FileWriter,
) {
    fun save() = writer.save()
    fun reload() = writer.reload()

    override fun toString() = "MinePactFile(path='$path', format=${type.formatName})"
}