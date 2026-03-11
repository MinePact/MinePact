package net.minepact.api.scheduler

class TimeInterval {

    companion object {
        const val MILLISECOND: Long = 1
        const val SECOND: Long = 1000 * MILLISECOND
        const val MINUTE: Long = 60 * SECOND
        const val HOUR: Long = 60 * MINUTE
        const val DAY: Long = 24 * HOUR
        const val WEEK: Long = 7 * DAY
        const val MONTH: Long = (365 / 12) * DAY
        const val YEAR: Long = 12 * MONTH

        fun format(time: Long): String {
            if (time <= 0) return "0ms"

            var remaining = time
            val parts = mutableListOf<String>()

            fun take(unit: Long, suffix: String) {
                val value = remaining / unit
                if (value > 0) {
                    parts += "$value$suffix"
                    remaining %= unit
                }
            }

            take(YEAR, "y")
            take(MONTH, "mo")
            take(WEEK, "w")
            take(DAY, "d")
            take(HOUR, "h")
            take(MINUTE, "m")
            take(SECOND, "s")
            take(MILLISECOND, "ms")

            return parts.joinToString(" ")
        }
    }
}