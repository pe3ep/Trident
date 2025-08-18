package cc.pe3epwithyou.trident.utils

import cc.pe3epwithyou.trident.Trident
import cc.pe3epwithyou.trident.config.Config
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component

object ChatUtils {
    fun info(s: String) {
        Trident.LOGGER.info("[Trident] $s")
    }

    fun debugLog(s: String) {
        if (Config.Debug.enableLogging) {
          Trident.LOGGER.info("[Trident] [DEBUG] $s")
        }
    }

    fun error(s: String) {
        Trident.LOGGER.error("[Trident] $s")
    }

    fun warn(s: String) {
        Trident.LOGGER.warn("[Trident] $s")
    }

    fun sendMessage(s: String, prefix: Boolean = true) {
        sendMessage(Component.literal(s), prefix)
    }

    fun sendMessage(c: Component, prefix: Boolean = true) {
        if (prefix) {
            Minecraft.getInstance().gui.chat.addMessage(TridentFont.tridentPrefix().append(c))
            return
        }
        Minecraft.getInstance().gui.chat.addMessage(c)
    }

}