package net.minepact.api.logging

import java.util.UUID

data class LogInfo(
    val serverId: UUID,
    val senderId: UUID,
    val type: LogType,
    val timestamp: Long,
    val content: String,
    val suspicious: Boolean
) {
    companion object {
        fun fromString(string: String): LogInfo {
            val parts = string
                .substringAfter("] ")
                .split(",")
                .map { it.trim() }

            val timestamp = parts[0].toLong()
            val serverId = UUID.fromString(parts[1])
            val senderId = UUID.fromString(parts[2])
            val type = LogType.valueOf(parts[3])
            val suspicious = parts[4].toBoolean()
            val content = string.substringAfter("] ")

            return LogInfo(serverId, senderId, type, timestamp, content, suspicious)
        }
    }
    override fun toString(): String {
        return "[$timestamp,$serverId,$senderId,$type,$suspicious] $content"
    }
}
