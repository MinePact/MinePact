package net.minepact.api.misc

import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

fun formatDuration(duration: Long): String {
    if (duration <= 0) {
        return ""
    }

    var seconds = (duration / 1_000).toInt()
    val days = seconds / 86_400
    seconds %= 86_400
    val hours = seconds / 3_600
    seconds %= 3_600
    val minutes = seconds / 60
    seconds %= 60

    val parts = mutableListOf<String>()
    if (days > 0) parts.add("${days}d")
    if (hours > 0) parts.add("${hours}h")
    if (minutes > 0) parts.add("${minutes}m")
    if (seconds > 0) parts.add("${seconds}s")

    return parts.joinToString(" ")
}
fun getLengthFromIdentifier(identifier: String): Long {
    val regex = Regex("(\\d+)([smhd])")
    val matchResult = regex.find(identifier) ?: return 0

    val value = matchResult.groupValues[1].toLong()
    val unit = matchResult.groupValues[2]

    return when (unit) {
        "s" -> value * 1_000
        "m" -> value * 60_000
        "h" -> value * 3_600_000
        "d" -> value * 86_400_000
        else -> 0
    }
}

fun formatDate(duration: Long): String {
    if (duration == Long.MIN_VALUE) {
        return "Never"
    }
    return SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Date(duration))
}
