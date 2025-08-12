package cc.pe3epwithyou.trident.widgets.questing

import cc.pe3epwithyou.trident.state.MCCGame
import cc.pe3epwithyou.trident.state.Rarity

class Quest(
    val game: MCCGame,
    val type: QuestType,
    val rarity: Rarity,
    val completionRequirement: QuestCompletionRequirement
)


class QuestCompletionRequirement(
    val criteria: CompletionCriteria,
    val initialProgress: Int,
    val totalProgress: Int,
    private val nameArg: String? = null
) {
    val name: String
        get() {
            val n = criteria.shortName
            if (nameArg != null) {
                n.replace("{value}", nameArg)
            }
            return n
        }
}

enum class QuestType {
    DEFAULT,
    SCROLL,
    BOOSTED,
    ARCANE,
}

enum class CompletionCriteria(val shortName: String) {

}