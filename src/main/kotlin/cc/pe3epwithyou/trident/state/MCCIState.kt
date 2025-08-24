package cc.pe3epwithyou.trident.state

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
        val server = Minecraft.getInstance().currentServer ?: return false
        return server.ip.contains("mccisland.net", true)
    }
}