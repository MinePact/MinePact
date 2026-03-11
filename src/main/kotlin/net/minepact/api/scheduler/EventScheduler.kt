package net.minepact.api.scheduler

import net.minepact.Main
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.ConcurrentHashMap
import java.time.Duration

object EventScheduler {
    val RUNNING_EVENTS: MutableMap<BukkitTask, TimedEvent> = ConcurrentHashMap()

    fun execute(event: TimedEvent) {
        execute(event, 0L)
    }

    fun execute(event: TimedEvent, delay: Long) {
        var task: BukkitTask? = null
        task = Bukkit.getScheduler().runTaskLater(
            Main.instance,
            Runnable {
                try {
                    event.run()
                } finally {
                    task?.let { RUNNING_EVENTS.remove(it) }
                }
            },
            delay / 50
        )

        RUNNING_EVENTS[task] = event
    }

    fun startTimedEvent(event: TimedEvent) {
        val initialDelay = calculateDelayUntil(event.startTime, event.interval) / 50

        val task = Bukkit.getScheduler().runTaskTimer(
            Main.instance,
            Runnable {
                if (event.cancelled) {
                    RUNNING_EVENTS.keys.firstOrNull { RUNNING_EVENTS[it] == event }?.cancel()
                    return@Runnable
                }

                event.run()

                if (event.interval <= 0) {
                    RUNNING_EVENTS.keys.firstOrNull { RUNNING_EVENTS[it] == event }?.cancel()
                }
            },
            initialDelay,
            if (event.interval > 0) event.interval / 50 else Long.MAX_VALUE
        )

        RUNNING_EVENTS[task] = event
    }

    fun calculateDelayUntil(startTime: String, intervalMillis: Long): Long {
        val now = LocalDateTime.now()
        val targetTime = LocalTime.parse(startTime)

        var baseTime = now.withHour(targetTime.hour)
            .withMinute(targetTime.minute)
            .withSecond(targetTime.second)
            .withNano(0)

        if (now.isBefore(baseTime)) {
            return Duration.between(now, baseTime).toMillis()
        }

        if (intervalMillis <= 0) {
            baseTime = baseTime.plusDays(1)
            return Duration.between(now, baseTime).toMillis()
        }

        val timeSinceBase = Duration.between(baseTime, now).toMillis()
        val remainder = timeSinceBase % intervalMillis

        if (remainder == 0L) {
            return 0L
        }

        val delay = intervalMillis - remainder
        return delay
    }
}