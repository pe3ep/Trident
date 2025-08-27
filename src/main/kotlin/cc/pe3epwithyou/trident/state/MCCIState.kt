package cc.pe3epwithyou.trident.state

import cc.pe3epwithyou.trident.config.Config
import net.minecraft.client.Minecraft

object Climate {
    var climateType: ClimateType = ClimateType.TEMPERATE
    var wayfinderData: Int = 0
}

object FishingState {
    var climate: Climate = Climate
    var isGrotto: Boolean = false
}

object MCCIState {
    var game: Game = Game.HUB
    var fishingState: FishingState = FishingState
    fun isOnIsland(): Boolean {
        if (Config.Debug.bypassOnIsland) return true
        val server = Minecraft.getInstance().currentServer ?: return false
        return server.ip.contains("mccisland.net", true)
    }
}