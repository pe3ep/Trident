package cc.pe3epwithyou.trident.feature

import cc.pe3epwithyou.trident.utils.Title
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.world.phys.Vec3

object DepletedDisplay {
    private const val DEPLETED_COLOR = 0xf27500
    private const val DEPLETED_COLOR_ALT = 0xfca600
    private val depletedTitle = Component.literal("This spot is ").withColor(DEPLETED_COLOR)
        .append(Component.literal("depleted").withColor(DEPLETED_COLOR_ALT))
    private val grottoTitle = Component.literal("Your ").withColor(DEPLETED_COLOR)
        .append(Component.literal("Grotto ").withColor(DEPLETED_COLOR_ALT))
        .append(Component.literal("is unstable").withColor(DEPLETED_COLOR))

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

    fun showGrottoTitle() {
        Title.sendTitle(
            Component.empty(),
            grottoTitle,
            5,
            110,
            15
        )
        DepletedTimer.INSTANCE.startLoop(grottoTitle, 110)
    }

    class DepletedTimer : ClientTickEvents.EndTick {
        private var ticksUntilSomething: Long = 0
        private var playerPosition: Vec3 = Vec3(0.0, 0.0, 0.0)
        private var title: Component = depletedTitle
        fun startLoop(component: Component, waitFor: Long) {
            this.playerPosition = Minecraft.getInstance().player?.position()!!
            this.ticksUntilSomething = waitFor
            this.title = component
        }

        override fun onEndTick(client: Minecraft) {
            if (--this.ticksUntilSomething == 0L) {
                Title.sendTitle(
                    Component.empty(),
                    this.title,
                    0,
                    10,
                    5,
                    false
                )
                if (Minecraft.getInstance().player?.position()!! == playerPosition) {
                    this.ticksUntilSomething = 2
                }
            }
        }

        companion object {
            val INSTANCE: DepletedTimer = DepletedTimer()
            fun register() {
                ClientTickEvents.END_CLIENT_TICK.register(INSTANCE)
            }
        }
    }
}