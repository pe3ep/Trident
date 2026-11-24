package cc.pe3epwithyou.trident.feature.dojo

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents

internal object DojoTickCounter {
    var ticks = 0L
        private set

    fun register() {
        ClientTickEvents.END_LEVEL_TICK.register {
            ticks++
        }
    }
}