package cc.pe3epwithyou.trident.state

import cc.pe3epwithyou.trident.Trident
import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.api.ApiChecker
import cc.pe3epwithyou.trident.feature.debug.DebugScreen
import cc.pe3epwithyou.trident.feature.discord.EventActivity
import cc.pe3epwithyou.trident.modrinth.UpdateChecker
import cc.pe3epwithyou.trident.utils.Logger
import cc.pe3epwithyou.trident.utils.ScoreboardUtils
import cc.pe3epwithyou.trident.utils.TridentFont.ERROR
import cc.pe3epwithyou.trident.utils.minecraft
import cc.pe3epwithyou.trident.utils.playerState
import com.noxcrew.noxesium.core.mcc.ClientboundMccGameStatePacket
import net.minecraft.network.chat.Component

data class Climate(
    var climateType: ClimateType = ClimateType.TEMPERATE, var wayfinderData: Int = 0
) {
    fun getCurrentWayfinderStatus(): WayfinderStatus {
        return when (this.climateType) {
            ClimateType.TEMPERATE -> playerState().wayfinderData.temperate
            ClimateType.TROPICAL -> playerState().wayfinderData.tropical
            ClimateType.BARREN -> playerState().wayfinderData.barren
        }
    }
}

data class FishingState(
    var climate: Climate = Climate(), var isGrotto: Boolean = false, var island: String? = null
)

object MCCIState {
    var game: Game = Game.HUB
    var lobbyGame: Game = Game.HUB
    var gameState: ClientboundMccGameStatePacket? = null
    var gameTypes: List<String> = emptyList()
    var currentServer: String = "lobby"
    var isPlobbyGame: Boolean = false
    var fishingState: FishingState = FishingState()
    fun isOnIsland(): Boolean {
        if (Config.Debug.bypassOnIsland) return true
        val server = minecraft().currentServer ?: return false
        return server.ip.contains("mccisland.net", true)
    }

    fun onJoin() = minecraft().execute {
        UpdateChecker.checkForUpdates()
        ApiChecker.joinCheck()
        DebugScreen.fetchMessages()
        LevelData.fetchData()
        EventActivity.fetchEventActivities()
        if (Trident.hasFailedToLoadConfig) {
            val component: Component =
                Component.translatable("trident.failed_config").withStyle(ERROR.baseStyle)
            Logger.sendMessage(component, true)
        }
    }

    fun isInPlobby(): Boolean {
        if (!isOnIsland()) return false
        if (isPlobbyGame) return true
        ScoreboardUtils.findInScoreboard(Regex("""PLOBBY \(\d+/\d+\):"""))?.let {
            return true
        }

        return false
    }
}