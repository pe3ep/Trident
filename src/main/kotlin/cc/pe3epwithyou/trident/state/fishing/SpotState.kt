package cc.pe3epwithyou.trident.state.fishing

data class SpotState(
    var hasSpot: Boolean = false,
    val hookPercents: MutableMap<UpgradeLine, Double> = mutableMapOf(),
    val magnetPercents: MutableMap<UpgradeLine, Double> = mutableMapOf(),
    var elusiveChanceBonusPercent: Double = 0.0,
    var pearlChanceBonusPercent: Double = 0.0,
    var treasureChanceBonusPercent: Double = 0.0,
    var spiritChanceBonusPercent: Double = 0.0,
    var wayfinderDataBonus: Double = 0.0,
    var fishChanceBonusPercent: Double = 0.0,
)


