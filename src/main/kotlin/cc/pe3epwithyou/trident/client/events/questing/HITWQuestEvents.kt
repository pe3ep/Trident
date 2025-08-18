package cc.pe3epwithyou.trident.client.events.questing

import cc.pe3epwithyou.trident.client.events.QuestIncrementContext
import cc.pe3epwithyou.trident.state.MCCGame
import cc.pe3epwithyou.trident.widgets.questing.CompletionCriteria
import cc.pe3epwithyou.trident.widgets.questing.QuestStorage
import net.minecraft.network.chat.Component

object HITWQuestEvents {
    fun scheduleSurvivedMinute() {
        QuestListener.handleTimedQuest(1L, true) {
            QuestStorage.applyIncrement(QuestIncrementContext(
                MCCGame.HITW,
                CompletionCriteria.HOLE_IN_THE_WALL_SURVIVED_MINUTE,
                1,
                "hitw_survived_60s"
            ))
        }
    }

    fun scheduleSurvivedTwoMinutes() {
        QuestListener.handleTimedQuest(2L, true) {
            QuestStorage.applyIncrement(QuestIncrementContext(
                MCCGame.HITW,
                CompletionCriteria.HOLE_IN_THE_WALL_SURVIVED_TWO_MINUTE,
                1,
                "hitw_survived_2m"
            ))
        }
    }

    fun handlePlacement(m: Component) {
        val match = Regex("(\\d+)(st|nd|rd|th) Place!").find(m.string) ?: return
        val placement = match.groups[1]?.value?.toInt() ?: return
        // helper to apply increments
        fun inc(criteria: CompletionCriteria, amount: Int = 1, tagSuffix: String? = null) {
            val tag = tagSuffix ?: "increment_${criteria.name.lowercase()}"
            QuestStorage.applyIncrement(
                QuestIncrementContext(
                    MCCGame.HITW,
                    criteria,
                    amount,
                    tag
                )
            )
        }

        if (placement <= 8) {
            inc(CompletionCriteria.HOLE_IN_THE_WALL_TOP_EIGHT, 1, "hitw_top8")
        }
        if (placement <= 5) {
            inc(CompletionCriteria.HOLE_IN_THE_WALL_TOP_FIVE, 1, "hitw_top5")
        }
        if (placement <= 3) {
            inc(CompletionCriteria.HOLE_IN_THE_WALL_TOP_THREE, 1, "hitw_top3")
        }
    }
}