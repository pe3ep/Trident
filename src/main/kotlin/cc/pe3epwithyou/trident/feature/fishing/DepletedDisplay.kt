package cc.pe3epwithyou.trident.feature.fishing

import cc.pe3epwithyou.trident.utils.Title
import cc.pe3epwithyou.trident.utils.extensions.CoroutineScopeExt.main
import kotlinx.coroutines.*
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.util.Util
import net.minecraft.world.phys.Vec3
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

object DepletedDisplay {
    private const val DEPLETED_COLOR = 0xf27500
    private const val DEPLETED_COLOR_ALT = 0xfca600
    private val depletedTitle = Component.literal("This spot is ").withColor(DEPLETED_COLOR)
        .append(Component.literal("depleted").withColor(DEPLETED_COLOR_ALT))

    fun showDepletedTitle() {
        Title.sendTitle(
            Component.empty(), depletedTitle, 5, 20, 15
        )
        DepletedTimer.startLoop(depletedTitle, 10)
    }

    object DepletedTimer {
        private var ticks: Long = 0
        private var playerPosition: Vec3 = Vec3(0.0, 0.0, 0.0)
        private var title: Component = depletedTitle
        private var castAt: Instant? = null
        private var job: Job? = null

        fun startLoop(component: Component, waitFor: Long) {
            this.playerPosition = Minecraft.getInstance().player?.position()!!
            this.ticks = waitFor * 50
            this.title = component
            this.castAt = Instant.now()

            this.job = CoroutineScope(Util.backgroundExecutor().asCoroutineDispatcher()).launch {
                while (true) {
                    delay(ticks)
                    ticks = 100
                    val currentPos =
                        Minecraft.getInstance().player?.position() ?: Vec3(0.0, 0.0, 0.0)
                    if ((castAt != null && hasHourPassed(castAt!!)) || currentPos != playerPosition) {
                        stopLoop()
                        break
                    }
                    main {
                        Title.sendTitle(
                            Component.empty(), title, 0, 10, 5, false
                        )
                    }
                }
            }
        }

        fun stopLoop() {
            playerPosition = Vec3(0.0, 0.0, 0.0)
            this.ticks = 0
            this.castAt = null
            job?.cancel()
        }

        fun hasHourPassed(saved: Instant, zone: ZoneId = ZoneId.systemDefault()): Boolean {
            val now = Instant.now().atZone(zone).toInstant()
            val savedHourStart = saved.atZone(zone).truncatedTo(ChronoUnit.HOURS).toInstant()
            val nextHourStart = savedHourStart.plus(1, ChronoUnit.HOURS)
            return !now.isBefore(nextHourStart)
        }
    }
}