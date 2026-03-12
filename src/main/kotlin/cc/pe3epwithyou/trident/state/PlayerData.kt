package cc.pe3epwithyou.trident.state

import cc.pe3epwithyou.trident.Trident
import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.utils.playerState

object PlayerData {
    var fetchedData: PlayerDataResponse? = null

    fun fetchData() {
        val provider = Config.Global.apiProvider
        provider.queryIslandData {
            onSuccess {
                fetchedData = it
                playerState().levelData = it.data.player.crownLevel
            }
            onError { _, throwable ->
                Trident.LOGGER.error("Failed to fetch player data", throwable)
            }
        }
    }
}