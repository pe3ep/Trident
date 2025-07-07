package cc.pe3epwithyou.trident.state

import cc.pe3epwithyou.trident.state.fishing.Augment
import net.minecraft.client.Minecraft

object MCCIslandState {
    var game: MCCGame = MCCGame.HUB

    fun isOnIsland(): Boolean {
        val server = Minecraft.getInstance().currentServer ?: return false
        return server.ip.contains("mccisland.net", true)
    }
}