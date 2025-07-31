package cc.pe3epwithyou.trident.client.events

import cc.pe3epwithyou.trident.state.MCCGame
import cc.pe3epwithyou.trident.state.MCCIslandState
import cc.pe3epwithyou.trident.utils.ChatUtils
import cc.pe3epwithyou.trident.widgets.killfeed.KillMethod
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.Util
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import java.util.Optional
import java.util.UUID

object KillChatListener {
    private val slainRegex = Regex("^\\[.] .+ (was slain by) .+")

    fun register() {
        ClientReceiveMessageEvents.ALLOW_GAME.register allowGame@{ message, _ ->
            if (!MCCIslandState.isOnIsland()) return@allowGame true
            if (MCCIslandState.game !in arrayOf(MCCGame.BATTLE_BOX, MCCGame.SKY_BATTLE)) return@allowGame true
            if (slainRegex.matches(message.string)) {
//                Remove the coins
//                message.siblings.forEach { c ->
//                    ChatUtils.sendMessage(c, true)
//                }
                val players = cleanupComponent(message)
                players.forEach {
                    ChatUtils.sendMessage(it, true)
                }
            }

            return@allowGame true
        }
    }

    private fun cleanupComponent(c: Component): List<Component> {
        val rawList = c.toFlatList()
        val socialManager = Minecraft.getInstance().playerSocialManager
        val components: MutableList<Component> = mutableListOf()
        rawList.forEach {
            if (it.string.length in 1..2) return@forEach
            val uuid = socialManager.getDiscoveredUUID(it.string)
            if (uuid == Util.NIL_UUID) return@forEach
            components.add(it)
            if (components.size == 2) return components
        }
        return components
    }
}