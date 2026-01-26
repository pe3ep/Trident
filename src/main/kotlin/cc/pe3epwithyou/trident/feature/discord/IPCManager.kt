package cc.pe3epwithyou.trident.feature.discord

import cc.pe3epwithyou.trident.Trident
import cc.pe3epwithyou.trident.utils.Logger
import io.github.vyfor.kpresence.RichClient
import io.github.vyfor.kpresence.event.ActivityUpdateEvent
import io.github.vyfor.kpresence.event.DisconnectEvent
import io.github.vyfor.kpresence.event.ReadyEvent
import io.github.vyfor.kpresence.rpc.ActivityBuilder

object IPCManager {
    private const val CLIENT_ID = 1464403445896314913

    var ipc: RichClient? = null

    /**
     * Submits an activity to the Discord IPC client for display as a Rich Presence.
     *
     * If the provided activity is null, the current activity will be cleared and removed from the display.
     * If a valid activity is provided, it will be updated and displayed on the user's Discord profile.
     *
     * @param activity An instance of [ActivityBuilder] that defines the details of the activity to display,
     *                 or null to clear the current activity.
     */
    fun submitBuilder(activity: ActivityBuilder?) {
        ipc?.let {
            val builtActivity = activity?.build()
            it.update(builtActivity)
            Logger.info("Submitted Discord activity: $builtActivity")
        }
    }

    fun init() {
        try {
            ipc = RichClient(CLIENT_ID)

            ipc!!.on<ReadyEvent> {
                Logger.info("Discord Presence Ready")
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

    fun stop() {
        try {
            ipc?.shutdown()
        } catch (t: Throwable) {
            Trident.LOGGER.warn("[Trident] Failed to disconnect from Discord IPC", t)
        }
    }
}