package cc.pe3epwithyou.trident.feature.questing

import cc.pe3epwithyou.trident.state.MCCGame

data class IncrementContext(
    val game: MCCGame,
    val criteria: QuestCriteria,
    val amount: Int = 1,
    val sourceTag: String? = null
)