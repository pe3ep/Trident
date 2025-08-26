package cc.pe3epwithyou.trident.feature.questing

import cc.pe3epwithyou.trident.state.Game

data class IncrementContext(
    val game: Game,
    val criteria: QuestCriteria,
    val amount: Int = 1,
    val sourceTag: String? = null
)