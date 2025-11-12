package cc.pe3epwithyou.trident.utils

import cc.pe3epwithyou.trident.Trident
import cc.pe3epwithyou.trident.config.Config
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component

object ChatUtils {
    private const val PREFIX = "[Trident]"

    fun info(s: String) {
        Trident.LOGGER.info("$PREFIX $s")
    }

    fun debugLog(s: String) {
        if (Config.Debug.enableLogging) {
            info("[DEBUG] $s")
        }
    }

    fun error(s: String) {
        Trident.LOGGER.error("$PREFIX $s")
    }

    fun warn(s: String) {
        Trident.LOGGER.warn("$PREFIX $s")
    }

    fun sendMessage(s: String, prefix: Boolean = true) {
        sendMessage(Component.literal(s), prefix)
    }

    fun sendMessage(c: Component, prefix: Boolean = true) {
        if (prefix) {
            Minecraft.getInstance().gui.chat.addMessage(TridentFont.tridentPrefix.append(c))
            return
        }
        Minecraft.getInstance().gui.chat.addMessage(c)
    }

}