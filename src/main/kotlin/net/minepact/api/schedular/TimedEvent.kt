package net.minepact.api.schedular

import java.lang.IllegalArgumentException

abstract class TimedEvent(
    var startTime: String = "00:00:00",
    var interval: Long = 0,
    var cancelled: Boolean = false
) {
    init {
        val timeFormat = Regex("^(?:[01]?\\d|2[0-3]):[0-5]\\d:[0-5]\\d$")

        if (!timeFormat.matches(startTime)) {
            throw IllegalArgumentException("[${this.javaClass.simpleName}] Constructor does not contain valid start time!")
        }
    }

    abstract fun run()
    open fun cancel() {
        cancelled = true
    }
}