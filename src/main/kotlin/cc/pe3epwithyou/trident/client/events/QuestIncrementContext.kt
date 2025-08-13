package cc.pe3epwithyou.trident.client.events

import cc.pe3epwithyou.trident.state.MCCGame
import cc.pe3epwithyou.trident.widgets.questing.CompletionCriteria

data class QuestIncrementContext(
    val game: MCCGame,
    val criteria: CompletionCriteria,
    val amount: Int = 1,
    val sourceTag: String? = null
)