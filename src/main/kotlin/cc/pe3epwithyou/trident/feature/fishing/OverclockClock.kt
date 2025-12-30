package cc.pe3epwithyou.trident.feature.fishing

import cc.pe3epwithyou.trident.interfaces.DialogCollection
import cc.pe3epwithyou.trident.state.OverclockState
import cc.pe3epwithyou.trident.utils.Logger
import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.TridentFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withSwatch
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvent
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object OverclockClock : ClientTickEvents.EndTick {
    val handlers = ConcurrentHashMap<String, ClockHandler>()

    fun register() {
        ClientTickEvents.END_CLIENT_TICK.register(this)
    }

    fun registerHandler(handler: ClockHandler) {
        handlers[handler.key] = handler
    }

    fun removeHandler(key: String) {
        handlers.remove(key)
    }

    override fun onEndTick(client: Minecraft) {
        handlers.values.forEach { handler ->
            handler.tick()
        }
    }

    class ClockHandler(val key: String, val state: OverclockState) {
        private var clockTimer: Int = 20
        fun tick() {
            if (clockTimer % 20 == 0) {
                val now = Instant.now().toEpochMilli()

                if (state.isActive) {
                    val left = state.activeUntil - now
                    if (Duration.ofMillis(left).toSeconds() <= 0L) {
                        state.isActive = false
                        state.isCooldown = true
                    }
                }
                if (state.isCooldown) {
                    val left = state.availableIn - now
                    if (Duration.ofMillis(left).toSeconds() <= 0L) {
                        state.isActive = false
                        state.isCooldown = false
                        sendReadyMessage(key)
                        removeHandler(key)
                    }
                }

                DialogCollection.refreshDialog("supplies")
            }
            clockTimer++
        }

        companion object {
            fun sendReadyMessage(oc: String) {
                val component =
                    Component.literal("Your ").withSwatch(TridentFont.TRIDENT_COLOR).append(
                        Component.literal("$oc Overclock").withSwatch(TridentFont.TRIDENT_ACCENT)
                    ).append(
                        Component.literal(" is no longer on cooldown and is ready to be used")
                            .withSwatch(TridentFont.TRIDENT_COLOR)
                    )
                Logger.sendMessage(component, true)
                Minecraft.getInstance().player?.playSound(
                    SoundEvent(Resources.mcc("games.fishing.overclock_ready"), Optional.empty())
                )
            }
        }
    }
}