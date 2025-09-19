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
        fun onCast(
            spot: FishingSpotListener.FishingSpot
        )
    }
}