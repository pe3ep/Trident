package cc.pe3epwithyou.trident.client.events.questing

import cc.pe3epwithyou.trident.client.events.QuestIncrementContext
import cc.pe3epwithyou.trident.state.MCCGame
import cc.pe3epwithyou.trident.widgets.questing.CompletionCriteria
import cc.pe3epwithyou.trident.widgets.questing.QuestStorage

object HITWQuestEvents {
    fun scheduleSurvivedMinuteHandler() {
        QuestListener.handleTimedQuests(1L) {
            QuestStorage.applyIncrement(QuestIncrementContext(
                MCCGame.HITW,
                CompletionCriteria.HOLE_IN_THE_WALL_SURVIVED_MINUTE,
                1,
                "hitw_survived_60s"
            ))
        }
    }

    fun scheduleSurvivedTwoMinutesHandler() {
        QuestListener.handleTimedQuests(2L) {
            QuestStorage.applyIncrement(QuestIncrementContext(
                MCCGame.HITW,
                CompletionCriteria.HOLE_IN_THE_WALL_SURVIVED_TWO_MINUTE,
                1,
                "hitw_survived_2m"
            ))
        }
    }
}