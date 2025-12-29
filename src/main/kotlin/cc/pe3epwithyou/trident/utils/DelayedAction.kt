package cc.pe3epwithyou.trident.utils

import cc.pe3epwithyou.trident.utils.extensions.CoroutineScopeExt.main
import kotlinx.coroutines.*
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.minecraft.util.Util
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object DelayedAction {
    private val tasks: ConcurrentHashMap<UUID, Job> = ConcurrentHashMap()

    @Suppress("unused")
    data class DelayedTask(val id: UUID) {
        /**
         * Cancel the underlying scheduled task.
         * @return true if the task was cancelled; false otherwise (already run or already cancelled).
         */
        fun cancel(): Boolean {
            val future = tasks.remove(id) ?: return false
            future.cancel()
            ChatUtils.debugLog("Task with id $id was cancelled")
            return true
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
     * Schedule an action to run after [delayMs] milliseconds.
     * Returns a DelayedTask that can be used to cancel the task.
     */
    fun delay(delayMs: Long, action: () -> Unit): DelayedTask {
        val id = UUID.randomUUID()
        val ctx = Util.backgroundExecutor().asCoroutineDispatcher()
        val future = CoroutineScope(ctx).launch {
            delay(delayMs)
            tasks.remove(id)
            main(action)
        }
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
            future.cancel()
        }
        tasks.clear()
    }

    /**
     * Cancel all pending scheduled tasks and shut down the executor.
     */
    private fun shutdown() {
        // Cancel tracked futures
        closeAllPendingTasks()
    }
}