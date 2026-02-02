package cc.pe3epwithyou.trident.state

import cc.pe3epwithyou.trident.Trident
import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.api.ApiProvider
import cc.pe3epwithyou.trident.utils.Logger
import cc.pe3epwithyou.trident.utils.NetworkUtil
import kotlinx.serialization.Serializable
import net.minecraft.client.Minecraft

@Serializable
data class LevelData(val crownLevel: ResponseLevelData, val fishingLevel: ResponseLevelData, val styleLevel: ResponseLevelData) {
    companion object {
        fun fetchData() {
            val uuid = Minecraft.getInstance().gameProfile.id.toString()

            val gql = """
                query CrownLevel {
                  player(uuid: "$uuid") {
                    crownLevel {
                      fishingLevelData {
                        evolution
                        level
                      }
                      levelData {
                        evolution
                        level
                      }
                      styleLevelData {
                        evolution
                        level
                      }
                    }
                  }
                }
            """.trimIndent()

            val headers = mutableMapOf<String, String>()
            val provider = Config.Global.apiProvider
            when (provider) {
                ApiProvider.TRIDENT -> headers["x-mc-uuid"] = uuid
                ApiProvider.SELF_TOKEN -> headers["X-API-Key"] = Config.Api.key
            }

            NetworkUtil.sendGraphQL<CrownLevelResponse>(provider.fetchUrl, gql, headers) {
                onSuccess { response ->
                    val data = LevelData(
                        response.data.player.crownLevel.levelData,
                        response.data.player.crownLevel.fishingLevelData,
                        response.data.player.crownLevel.styleLevelData
                    )

                    Trident.playerState.levelData = data

                    Logger.debugLog("Fetched level data: $data")
                }
            }
        }

        @Serializable
        private data class CrownLevelResponse(
            val data: Data,
        )

        @Serializable
        private data class Data(
            val player: Player,
        )

        @Serializable
        private data class Player(
            val crownLevel: CrownLevel,
        )

        @Serializable
        private data class CrownLevel(
            val fishingLevelData: ResponseLevelData,
            val levelData: ResponseLevelData,
            val styleLevelData: ResponseLevelData,
        )

        @Serializable
        data class ResponseLevelData(
            val evolution: Int,
            val level: Int,
        )
    }
}


