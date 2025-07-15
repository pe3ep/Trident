package cc.pe3epwithyou.trident

import net.fabricmc.api.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Trident : ModInitializer {
    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(this.toString())
    }
    override fun onInitialize() {
        LOGGER.info("[Trident] Initializing Client...")
    }
}
