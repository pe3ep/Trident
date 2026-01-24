package cc.pe3epwithyou.trident.feature.discord

import cc.pe3epwithyou.trident.Trident
import cc.pe3epwithyou.trident.utils.Logger
import dev.cbyrne.kdiscordipc.KDiscordIPC
import dev.cbyrne.kdiscordipc.core.event.impl.DisconnectedEvent
import dev.cbyrne.kdiscordipc.core.event.impl.ErrorEvent
import dev.cbyrne.kdiscordipc.core.event.impl.ReadyEvent
import dev.cbyrne.kdiscordipc.data.activity.Activity
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest

object IPCManager {
    private const val CLIENT_ID = "1464403445896314913"

    val ipc = KDiscordIPC(CLIENT_ID)

    private var job: Job? = null

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Trident.LOGGER.error("[Trident] Unhandled exception in RichPresence coroutine", throwable)
    }

    private val activityUpdates = MutableSharedFlow<Activity?>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )


    fun submitActivity(activity: Activity?) {
        activityUpdates.tryEmit(activity)
        Logger.info("Submitted Discord activity: $activity")
    }

    val scope = CoroutineScope(
        SupervisorJob()
                + Dispatchers.IO
                + CoroutineName("Trident-RPC")
                + coroutineExceptionHandler
    )

    fun init() {
        if (job != null) return

        job = scope.launch {
            ipc.on<ReadyEvent> {
                Logger.info("Discord Presence Ready for (${data.user.username}#${data.user.discriminator})")
            }

            ipc.on<DisconnectedEvent> {
                Logger.info("Discord Presence Disconnected")
            }

            ipc.on<ErrorEvent> {
                Logger.error("Discord IPC Error: ${data.message}")
            }

            val collector = launch {
                activityUpdates.collectLatest { activity ->
                    val ok = withTimeoutOrNull(5_000L) {
                        Logger.info("Sending activity $activity to Discord")
                        ipc.activityManager.setActivity(activity)
                        true
                    } ?: false

                    if (!ok) {
                        Logger.info("Failed to set Discord activity (timeout/failed), will try next update")
                    }
                }
            }

            try {
                ipc.connect()
            } catch (t: Throwable) {
                Trident.LOGGER.error("[Trident] Failed to connect to Discord IPC", t)
            } finally {
                collector.cancelAndJoin()
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
        try {
            ipc.disconnect()
        } catch (t: Throwable) {
            Trident.LOGGER.warn("[Trident] Failed to disconnect from Discord IPC", t)
        }
    }

    fun shutdown() {
        stop()
        scope.cancel()
    }
}