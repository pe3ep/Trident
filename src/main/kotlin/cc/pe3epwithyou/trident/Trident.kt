package cc.pe3epwithyou.trident

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.modrinth.UpdateChecker
import cc.pe3epwithyou.trident.utils.NoxesiumUtils
import net.fabricmc.api.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Trident : ModInitializer {
    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(this.toString())
    }
    override fun onInitialize() {
        LOGGER.info("[Trident] Initializing Client...")
        Config.init()
        NoxesiumUtils.registerListeners()
        UpdateChecker.init()
    }
}
