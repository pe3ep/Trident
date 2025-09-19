package cc.pe3epwithyou.trident.client.events

import cc.pe3epwithyou.trident.client.listeners.FishingSpotListener
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory

object FishingSpotEvents {
    val CAST: Event<FishingSpotEventCallback> = EventFactory.createArrayBacked(
        FishingSpotEventCallback::class.java
    ) { listeners ->
        FishingSpotEventCallback { spot ->
            for (l in listeners) {
                l.onCast(spot)
            }
        }
    }

    fun interface FishingSpotEventCallback {
        /**
         * Called when fishing rod is cast in a new spot.
         * Isn't called when player re-casts their rod into the same spot.
         * @param spot The new spot
         */
        fun onCast(
            spot: FishingSpotListener.FishingSpot
        )
    }
}