package cc.pe3epwithyou.trident.client.events

import cc.pe3epwithyou.trident.feature.killfeed.KillMethod
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory

/**
 * Used for other mods to piggy-pack of Trident's kill detection
 */
object KillEvents {
    val KILL: Event<KillEventCallback> = EventFactory.createArrayBacked(
        KillEventCallback::class.java
    ) { listeners ->
        KillEventCallback { victim, attacker, killMethod ->
            for (l in listeners) {
                if (l.onKill(victim, attacker, killMethod)) return@KillEventCallback true
            }
            false
        }
    }

    fun interface KillEventCallback {
        fun onKill(
            victim: KillEventPlayer,
            attacker: KillEventPlayer?,
            killMethod: KillMethod
        ): Boolean
    }

    data class KillEventPlayer(val name: String, val teamColor: Int)
}