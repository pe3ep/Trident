package cc.pe3epwithyou.trident.utils

import cc.pe3epwithyou.trident.client.events.questing.QuestListener
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.minecraft.client.Minecraft
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

object DelayedAction {
    private val executorService: ScheduledExecutorService =
        Executors.newSingleThreadScheduledExecutor { r ->
            Thread(r, "trident-delay-thread").apply { isDaemon = true }
        }

    private val tasks: ConcurrentHashMap<UUID, ScheduledFuture<*>?> = ConcurrentHashMap()

    data class DelayedTask(val id: UUID) {
        /**
         * Cancel the underlying scheduled task.
         * @return true if the task was cancelled; false otherwise (already run or already cancelled).
         */
        fun cancel(): Boolean {
            val future = tasks.remove(id)
            ChatUtils.debugLog("Task with id $id was cancelled")
            return future?.cancel(false) ?: false
        }

        /**
         * Returns true if the task was cancelled (or not present).
         */
        fun isCancelled(): Boolean {
            val future = tasks[id]
            return future?.isCancelled ?: true
        }
    }

    fun init() {
        // Clean up when client stops
        ClientLifecycleEvents.CLIENT_STOPPING.register { shutdown() }
    }

    /**
     * Schedule an action to run after [delay] milliseconds.
     * Returns a DelayedTask that can be used to cancel the task.
     */
    fun delay(delay: Long, action: () -> Unit): DelayedTask {
        val id = UUID.randomUUID()
        val future: ScheduledFuture<*> = executorService.schedule({
            // Remove from map as it's about to run (or already running)
            tasks.remove(id)
            QuestListener.interruptibleTasks.remove(id)
            // enqueue back to Minecraft client (main) thread
            Minecraft.getInstance().execute(action)
        }, delay, TimeUnit.MILLISECONDS)

        tasks[id] = future
        return DelayedTask(id)
    }

    /**
     * Schedule an action to run after [ticks] Minecraft ticks.
     * Returns a DelayedTask that can be used to cancel the task.
     */
    fun delayTicks(ticks: Long, action: () -> Unit): DelayedTask {
        return delay(ticks * 50, action)
    }

    fun closeAllPendingTasks() {
        for ((_, future) in tasks) {
            future?.cancel(false)
        }
        tasks.clear()
    }

    /**
     * Cancel all pending scheduled tasks and shut down the executor.
     */
    private fun shutdown() {
        // Cancel tracked futures
        closeAllPendingTasks()

        executorService.shutdownNow()
    }
}