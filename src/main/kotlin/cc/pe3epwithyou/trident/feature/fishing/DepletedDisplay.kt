package cc.pe3epwithyou.trident.feature.fishing

import cc.pe3epwithyou.trident.utils.Title
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
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
            Component.empty(),
            depletedTitle,
            5,
            20,
            15
        )
        DepletedTimer.INSTANCE.startLoop(depletedTitle, 10)
    }

    class DepletedTimer : ClientTickEvents.EndTick {
        private var ticks: Long = 0
        private var playerPosition: Vec3 = Vec3(0.0, 0.0, 0.0)
        private var title: Component = depletedTitle
        private var castAt: Instant? = null
        fun startLoop(component: Component, waitFor: Long) {
            this.playerPosition = Minecraft.getInstance().player?.position()!!
            this.ticks = waitFor
            this.title = component
            this.castAt = Instant.now()
        }

        fun stopLoop() {
            playerPosition = Vec3(0.0, 0.0, 0.0)
            this.ticks = 0
            this.castAt = null
        }

        override fun onEndTick(client: Minecraft) {
            if (--this.ticks == 0L) {
                if (castAt != null && hasHourPassed(castAt!!)) {
                    stopLoop()
                }
                Title.sendTitle(
                    Component.empty(),
                    this.title,
                    0,
                    10,
                    5,
                    false
                )
                if ((Minecraft.getInstance().player?.position() ?: Vec3(0.0, 0.0, 0.0)) == playerPosition) {
                    this.ticks = 2
                }
            }
        }

        companion object {
            val INSTANCE: DepletedTimer = DepletedTimer()
            fun register() {
                ClientTickEvents.END_CLIENT_TICK.register(INSTANCE)
            }
        }

        fun hasHourPassed(saved: Instant, zone: ZoneId = ZoneId.systemDefault()): Boolean {
            val now = Instant.now().atZone(zone).toInstant()
            val savedHourStart = saved.atZone(zone)
                .truncatedTo(ChronoUnit.HOURS)
                .toInstant()
            val nextHourStart = savedHourStart.plus(1, ChronoUnit.HOURS)
            return !now.isBefore(nextHourStart)
        }
    }
}