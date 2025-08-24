package cc.pe3epwithyou.trident.feature.questing.game

import cc.pe3epwithyou.trident.feature.questing.IncrementContext
import cc.pe3epwithyou.trident.feature.questing.QuestCriteria
import cc.pe3epwithyou.trident.feature.questing.QuestListener
import cc.pe3epwithyou.trident.feature.questing.QuestStorage
import cc.pe3epwithyou.trident.state.Game
import net.minecraft.network.chat.Component

object RSRHandlers {
    private fun inc(criteria: QuestCriteria, tagSuffix: String? = null, canBeDuplicated: Boolean = false) {
        val tag = tagSuffix ?: "increment_${criteria.name.lowercase()}"
        QuestStorage.applyIncrement(
            IncrementContext(
                Game.ROCKET_SPLEEF_RUSH,
                criteria,
                1,
                tag
            ),
            canBeDuplicated
        )
    }

    fun scheduleSurvivedMinute() {
        QuestListener.handleTimedQuest(1L, true) {
            QuestStorage.applyIncrement(
                IncrementContext(
                    Game.ROCKET_SPLEEF_RUSH,
                    QuestCriteria.ROCKET_SPLEEF_SURVIVE_60S,
                    1,
                    "rsr_survived_60s"
                )
            )
        }
    }

    fun handle(m: Component) {
        val elimination = Regex("^\\[.] ((.+) was (eliminated|spleefed) by (.+)|(.+) died) \\[.+]").find(m.string)
        if (elimination != null) {
            inc(QuestCriteria.ROCKET_SPLEEF_PLAYERS_OUTLIVED, "rsr_players_outlived", true)
        }

        val death = Regex("^\\[.] .+, you were eliminated in (\\d+)(st|nd|rd|th)").find(m.string)
        if (death != null) {
            val placement = death.groups[1]?.value?.toInt() ?: return

            if (placement <= 8) {
                inc(QuestCriteria.ROCKET_SPLEEF_TOP_EIGHT, "rsr_top8")
            }
            if (placement <= 5) {
                inc(QuestCriteria.ROCKET_SPLEEF_TOP_FIVE, "rsr_top5")
            }
            if (placement <= 3) {
                inc(QuestCriteria.ROCKET_SPLEEF_TOP_THREE, "rsr_top3")
            }
        }
    }
}