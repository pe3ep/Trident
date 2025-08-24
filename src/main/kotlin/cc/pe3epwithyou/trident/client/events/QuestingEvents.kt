package cc.pe3epwithyou.trident.client.events

import cc.pe3epwithyou.trident.feature.questing.IncrementContext
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory

object QuestingEvents {
    val INCREMENT_ACTIVE: Event<QuestingEventCallback> = EventFactory.createArrayBacked(
        QuestingEventCallback::class.java
    ) { listeners ->
        QuestingEventCallback { ctx ->
            for (l in listeners) {
                if (l.onIncrement(ctx)) return@QuestingEventCallback true
            }
            false
        }
    }

    fun interface QuestingEventCallback {
        /**
         * Return true to consume/cancel further handling.
         * Returning false allows other listeners to also process the increment.
         */
        fun onIncrement(ctx: IncrementContext): Boolean
    }
}