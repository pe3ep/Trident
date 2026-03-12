package cc.pe3epwithyou.trident.state

import cc.pe3epwithyou.trident.feature.exchange.Collections
import kotlinx.serialization.Serializable

@Serializable
data class PlayerDataResponse(
    val data: PlayerDataResponsePlayer,
)

@Serializable
data class PlayerDataResponsePlayer(
    val player: Player,
)

@Serializable
data class Player(
    val collections: Collections,
    val crownLevel: CrownLevel,
)

@Serializable
data class CrownLevel(
    val fishingLevelData: FishingLevelData,
    val levelData: LevelData,
    val styleLevelData: StyleLevelData,
)

@Serializable
data class FishingLevelData(
    val evolution: Int,
    val level: Int,
)

@Serializable
data class LevelData(
    val evolution: Int,
    val level: Int,
)

@Serializable
data class StyleLevelData(
    val evolution: Int,
    val level: Int,
)

