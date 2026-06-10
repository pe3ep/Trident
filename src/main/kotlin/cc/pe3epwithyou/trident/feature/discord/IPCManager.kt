package cc.pe3epwithyou.trident.feature.discord

import cc.pe3epwithyou.trident.Trident
import cc.pe3epwithyou.trident.utils.Logger
import cc.pe3epwithyou.trident.utils.TridentFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.withSwatch
import cc.pe3epwithyou.trident.utils.main
import cc.pe3epwithyou.trident.utils.nonCriticalIO
import io.github.vyfor.kpresence.RichClient
import io.github.vyfor.kpresence.event.ActivityUpdateEvent
import io.github.vyfor.kpresence.event.DisconnectEvent
import io.github.vyfor.kpresence.event.ReadyEvent
import io.github.vyfor.kpresence.rpc.ActivityBuilder
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import net.minecraft.network.chat.Component
import kotlin.time.Duration.Companion.milliseconds

object IPCManager {
    private const val CLIENT_ID = 1464403445896314913

    var ipc: RichClient? = null

    /**
     * Submits an activity to the Discord IPC client for display as a Rich Presence.
     *
     * If the provided activity is null, the current activity will be cleared and removed from the display.
     * If a valid activity is provided, it will be updated and displayed on the user's Discord profile.
     *
     * @param builder An instance of [ActivityBuilder] that defines the details of the activity to display,
     *                 or null to clear the current activity.
     */
    fun submitBuilder(builder: ActivityBuilder?) {
        ipc?.let {
            it.coroutineScope.launch {
                if (builder == null) {
                    try {
                        it.update(null)
                    } catch (e: Exception) {
                        Logger.error("Failed to clear Discord activity, restarting IPC", e)
                        restart()
                    }
                    return@launch
                }

                withTimeoutOrNull(5_000L.milliseconds) {
                    try {
                        val builtActivity = builder.build()
                        it.update(builtActivity)
                        Logger.info("Submitted Discord activity: $builtActivity")
                    } catch (e: Exception) {
                        Logger.error("Failed to submit Discord activity: $builder", e)
                        stop()
                    }
                } ?: run {
                    Logger.error("Failed to submit Discord activity: $builder")
                }
            }
            return
        }

        // IPC is not initialized, initialize and submit again
        Logger.info("Discord IPC connection not established, initializing...")
        init(builder)
    }

    fun init(initialBuilder: ActivityBuilder? = null) = nonCriticalIO().launch {
        try {
            ipc = RichClient(CLIENT_ID)
            ipc!!.on<ReadyEvent> {
                Logger.info("Discord Presence Ready")
                initialBuilder?.let {
                    submitBuilder(it)
                }
            }

            ipc!!.on<DisconnectEvent> {
                Logger.info("Discord Presence Disconnected")
            }

            ipc!!.on<ActivityUpdateEvent> {
                Logger.info("Discord Activity Updated")
            }

            ipc!!.connect()
        } catch (e: Exception) {
            Trident.LOGGER.error("[Trident] Failed to initialize RichPresence", e)
        }
    }

    fun stop() = nonCriticalIO().launch {
        try {
            ipc?.shutdown()
            ipc = null
        } catch (t: Throwable) {
            Logger.error("[Trident] Failed to disconnect from Discord IPC", t)
        }
    }

    fun restart(sendMessage: Boolean = false) {
        nonCriticalIO().launch {
            try {
                withTimeoutOrNull(3_000) {
                    ipc?.shutdown()
                    ipc = null
                    ActivityManager.updateCurrentActivity()
                    main {
                        if (sendMessage) {
                            Logger.sendMessage(
                                Component.literal("Successfully reconnected to Discord")
                                    .withSwatch(TridentFont.TRIDENT_ACCENT)
                            )
                        }
                    }
                } ?: main {
                    if (sendMessage) {
                        Logger.sendMessage(
                            Component.literal("Timed out when reconnecting to Discord. Check your internet connection.")
                                .withSwatch(TridentFont.ERROR)
                        )
                    }
                }
            } catch (e: Exception) {
                main {
                    Logger.error("Failed to reconnect to Discord", e)
                    if (sendMessage) {
                        Logger.sendMessage(
                            Component.literal("Failed to reconnect to Discord. Check your game console for errors.")
                        )
                    }
                }
            }
        }
    }
}