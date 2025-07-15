package cc.pe3epwithyou.trident.client.events

import cc.pe3epwithyou.trident.client.TridentClient
import cc.pe3epwithyou.trident.utils.ChatUtils
import cc.pe3epwithyou.trident.utils.TridentFont
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component

class ChatEventListener {
    private fun handleMessage(message: Component) {
        if (!TridentClient.DEBUG_MODE) return
        val raw = message.string
        ChatUtils.sendMessage(raw)
    }


    fun register() {
        ClientReceiveMessageEvents.GAME.register { message, isActionbar ->
            if (!isActionbar) handleMessage(message)
        }
    }
}