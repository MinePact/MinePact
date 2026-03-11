package net.minepact.core.global.events.timed

import net.minepact.Main
import net.minepact.api.player.PlayerRegistry
import net.minepact.api.scheduler.TimeInterval
import net.minepact.api.scheduler.TimedEvent
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask

class RestartEvent(
    var waitPeriod: Long = TimeInterval.MINUTE * 30 // default
) : TimedEvent(
    startTime = "18:00:00",
    interval = TimeInterval.DAY,
    cancelled = false
) {
    private var task: BukkitTask? = null
    private var remainingTime: Long = waitPeriod

    override fun run() {
        if (cancelled) return

        remainingTime = waitPeriod.coerceAtMost(TimeInterval.HOUR)

        task = Bukkit.getScheduler().runTaskTimer(Main.instance, Runnable {
            if (cancelled) {
                task?.cancel()
                return@Runnable
            }

            if (shouldShowTitle(remainingTime)) {
                PlayerRegistry.online().forEach { it.title("<green>Restarting in: <red><b>${TimeInterval.format(remainingTime)}</b>") }
            }

            remainingTime -= TimeInterval.SECOND

            if (remainingTime <= 0) {
                task?.cancel()
                Main.RESTARTING = true
                Bukkit.getServer().restart()
            }
        }, 0L, TimeInterval.SECOND / 50)
    }
    override fun cancel() {
        super.cancel()
        task?.cancel()

        PlayerRegistry.online().forEach { it.title("<red><b>Restart cancelled!</b>") }
    }

    private fun shouldShowTitle(time: Long): Boolean {
        return when {
            time >= TimeInterval.HOUR -> time == TimeInterval.HOUR
            time >= TimeInterval.MINUTE * 30 -> time % (TimeInterval.MINUTE * 30) == 0L
            time >= TimeInterval.MINUTE * 15 -> time % (TimeInterval.MINUTE * 15) == 0L
            time >= TimeInterval.MINUTE * 5 -> time % (TimeInterval.MINUTE * 5) == 0L
            time >= TimeInterval.MINUTE -> time % TimeInterval.MINUTE == 0L
            time <= TimeInterval.SECOND * 5 -> true // last 5 seconds
            else -> false
        }
    }
}