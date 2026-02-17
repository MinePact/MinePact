package net.minepact.api.schedular

import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.ConcurrentHashMap
import java.time.Duration

object EventSchedular {
    val RUNNING_EVENTS: MutableMap<Thread, TimedEvent> = ConcurrentHashMap()

    fun execute(event: TimedEvent) {
        execute(event, 0L)
    }
    fun execute(event: TimedEvent, delay: Long) {
        val thread = Thread {
            if (delay > 0) Thread.sleep(delay)

            try {
                event.run()
            } finally {
                RUNNING_EVENTS.remove(Thread.currentThread())
            }
        }

        RUNNING_EVENTS[thread] = event
        thread.start()
    }

    fun startTimedEvent(event: TimedEvent) {
        val thread = Thread {
            try {
                while (!event.cancelled) {
                    val delay = calculateDelayUntil(event.startTime)
                    Thread.sleep(delay)
                    if (event.cancelled) break
                    event.run()

                    if (event.interval > 0) {
                        Thread.sleep(event.interval)
                    } else {
                        break
                    }
                }
            } finally {
                RUNNING_EVENTS.remove(Thread.currentThread())
            }
        }

        RUNNING_EVENTS[thread] = event
        thread.start()
    }
    private fun calculateDelayUntil(startTime: String): Long {
        val now = LocalDateTime.now()
        val targetTime = LocalTime.parse(startTime)

        var nextRun = now.withHour(targetTime.hour)
            .withMinute(targetTime.minute)
            .withSecond(targetTime.second)
            .withNano(0)

        if (nextRun.isBefore(now)) {
            nextRun = nextRun.plusDays(1)
        }

        return Duration.between(now, nextRun).toMillis()
    }
}