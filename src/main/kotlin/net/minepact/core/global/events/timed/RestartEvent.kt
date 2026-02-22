package net.minepact.core.global.events.timed

import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.title.Title
import net.minepact.Main
import net.minepact.api.schedular.TimeInterval
import net.minepact.api.schedular.TimedEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class RestartEvent(
    var waitPeriod: Long = TimeInterval.MINUTE * 30
) : TimedEvent(
    startTime = "20:00:00",
    interval = TimeInterval.DAY,
    cancelled = false
) {
    override fun run() {
        while (true) {
            if (cancelled) break

            if (waitPeriod > TimeInterval.HOUR) {
                waitPeriod = TimeInterval.HOUR
            }

            if (waitPeriod <= TimeInterval.SECOND * 10) {
                if (waitPeriod == 0L) {
                    Thread.sleep(TimeInterval.SECOND / 2)
                    Main.RESTARTING = true
                    Bukkit.getServer().restart()
                    break
                }
                Bukkit.getOnlinePlayers().forEach { player -> title(waitPeriod, player) }
                waitPeriod -= TimeInterval.SECOND
                Thread.sleep(TimeInterval.SECOND)
                continue
            }

            if ((waitPeriod * 10) % (30 * TimeInterval.SECOND) == 0L) {
                Bukkit.getOnlinePlayers().forEach { player -> title(waitPeriod, player) }
            }

            waitPeriod -= TimeInterval.SECOND
            Thread.sleep(TimeInterval.SECOND)
        }
    }
    override fun cancel() {
        super.cancel()

        Bukkit.getOnlinePlayers().forEach { player -> run { player.showTitle(Title.title(
            MiniMessage.miniMessage().deserialize("<red><b>Restart cancelled!</b>"),
            MiniMessage.miniMessage().deserialize("")
        )) } }
    }

    fun title(time: Long, player: Player) {
        val timeLeft: String = TimeInterval.format(time)

        player.showTitle(Title.title(
            MiniMessage.miniMessage().deserialize("<green>Restarting in: <red><b>${timeLeft}</b>"),
            MiniMessage.miniMessage().deserialize("")
        ))
    }
}