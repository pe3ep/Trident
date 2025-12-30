package cc.pe3epwithyou.trident.client

import cc.pe3epwithyou.trident.utils.NoxesiumUtils
import com.noxcrew.noxesium.core.fabric.mcc.MccNoxesiumEntrypoint

class TridentEntrypoint : MccNoxesiumEntrypoint() {
    override fun getVersion(): String = "1"

    override fun initialize() {
        NoxesiumUtils.registerListeners()
    }

}
