package cc.pe3epwithyou.trident.client.events.questing

import cc.pe3epwithyou.trident.client.events.QuestIncrementContext
import cc.pe3epwithyou.trident.state.MCCGame
import cc.pe3epwithyou.trident.widgets.questing.CompletionCriteria
import cc.pe3epwithyou.trident.widgets.questing.QuestStorage
import net.minecraft.network.chat.Component

object RocketSpleefRushQuestEvents {
    private fun inc(criteria: CompletionCriteria, tagSuffix: String? = null, canBeDuplicated: Boolean = false) {
        val tag = tagSuffix ?: "increment_${criteria.name.lowercase()}"
        QuestStorage.applyIncrement(
            QuestIncrementContext(
                MCCGame.ROCKET_SPLEEF_RUSH,
                criteria,
                1,
                tag
            ),
            canBeDuplicated
        )
    }

    fun scheduleSurvivedMinute() {
        QuestListener.handleTimedQuest(1L, true) {
            QuestStorage.applyIncrement(QuestIncrementContext(
                MCCGame.ROCKET_SPLEEF_RUSH,
                CompletionCriteria.ROCKET_SPLEEF_SURVIVE_60S,
                1,
                "rsr_survived_60s"
            ))
        }
    }

    fun handleRocketSpleefRush(m: Component) {
        val elimination = Regex("^\\[.] ((.+) was (eliminated|spleefed) by (.+)|(.+) died) \\[.+]").find(m.string)
        if (elimination != null) {
            inc(CompletionCriteria.ROCKET_SPLEEF_PLAYERS_OUTLIVED, "rsr_players_outlived", true)
        }

        val death = Regex("^\\[.] .+, you were eliminated in (\\d+)(st|nd|rd|th)").find(m.string)
        if (death != null) {
            val placement = death.groups[1]?.value?.toInt() ?: return

            if (placement <= 8) {
                inc(CompletionCriteria.ROCKET_SPLEEF_TOP_EIGHT, "rsr_top8")
            }
            if (placement <= 5) {
                inc(CompletionCriteria.ROCKET_SPLEEF_TOP_FIVE, "rsr_top5")
            }
            if (placement <= 3) {
                inc(CompletionCriteria.ROCKET_SPLEEF_TOP_THREE, "rsr_top3")
            }
        }
    }
}