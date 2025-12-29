package cc.pe3epwithyou.trident.feature.fishing

import cc.pe3epwithyou.trident.state.OverclockState
import cc.pe3epwithyou.trident.state.PlayerStateIO
import java.time.Duration
import java.time.Instant

object OverclockHandlers {
    fun startTimedOverclock(name: String, overclock: OverclockState) {
        overclock.isActive = true
        overclock.isCooldown = false

        val now = Instant.now().toEpochMilli()
        val activeUntil = now + Duration.ofSeconds(overclock.duration).toMillis()
        val availableIn =
            now + Duration.ofSeconds(overclock.duration)
                .toMillis() + Duration.ofSeconds(overclock.cooldownDuration)
                .toMillis()

        overclock.activeUntil = activeUntil
        overclock.availableIn = availableIn
        OverclockClock.registerHandler(OverclockClock.ClockHandler(name, overclock))
        PlayerStateIO.save()
    }
}