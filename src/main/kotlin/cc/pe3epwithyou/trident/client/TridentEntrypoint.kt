package cc.pe3epwithyou.trident.client

import com.noxcrew.noxesium.core.fabric.mcc.MccNoxesiumEntrypoint

class TridentEntrypoint : MccNoxesiumEntrypoint() {
    override fun initialize() {
        NoxesiumManager.registerListeners()
    }
}