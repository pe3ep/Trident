package cc.pe3epwithyou.trident.utils

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.minecraft.client.Minecraft
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

object DelayedAction {
    private val executorService: ScheduledExecutorService =
        Executors.newSingleThreadScheduledExecutor { r ->
            Thread(r, "trident-delay-thread").apply { isDaemon = true }
        }

    fun init() {
        // Clean up when client stops
        ClientLifecycleEvents.CLIENT_STOPPING.register { shutdown() }
    }

    fun delay(delay: Long, action: () -> Unit) {
        executorService.schedule({
            // enqueue back to Minecraft client (main) thread
            Minecraft.getInstance().execute(action)
        }, delay, TimeUnit.MILLISECONDS)
    }

    fun delayTicks(ticks: Long, action: () -> Unit) {
        delay(ticks * 50, action)
    }

    fun shutdown() {
        executorService.shutdownNow()
    }
}