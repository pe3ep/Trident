package cc.pe3epwithyou.trident.state

import cc.pe3epwithyou.trident.Trident
import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.DebugScreen
import cc.pe3epwithyou.trident.feature.api.ApiChecker
import cc.pe3epwithyou.trident.modrinth.UpdateChecker
import cc.pe3epwithyou.trident.utils.Logger
import cc.pe3epwithyou.trident.utils.TridentFont.ERROR
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component

data class Climate(
    var climateType: ClimateType = ClimateType.TEMPERATE, var wayfinderData: Int = 0
)

data class FishingState(
    var climate: Climate = Climate(), var isGrotto: Boolean = false
)

object MCCIState {
    var game: Game = Game.HUB
    var isPlobby: Boolean = false
    var fishingState: FishingState = FishingState()
    fun isOnIsland(): Boolean {
        if (Config.Debug.bypassOnIsland) return true
        val server = Minecraft.getInstance().currentServer ?: return false
        return server.ip.contains("mccisland.net", true)
    }

    fun onJoin() {
        UpdateChecker.checkForUpdates()
        ApiChecker.joinCheck()
        DebugScreen.fetchMessages()
        if (Trident.hasFailedToLoadConfig) {
            val component: Component =
                Component.translatable("trident.failed_config").withStyle(ERROR.baseStyle)
            Logger.sendMessage(component, true)
        }
    }
}