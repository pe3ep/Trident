package cc.pe3epwithyou.trident.feature.questing.game

import cc.pe3epwithyou.trident.feature.questing.IncrementContext
import cc.pe3epwithyou.trident.feature.questing.QuestCriteria
import cc.pe3epwithyou.trident.feature.questing.QuestStorage
import cc.pe3epwithyou.trident.state.Game
import cc.pe3epwithyou.trident.utils.TimeUtil
import net.minecraft.network.chat.Component
import java.util.concurrent.TimeUnit

object PKWDojoHandlers {
    private const val STANDARD_CMPL = 0
    private const val ADVANCED_CMPL = 1
    private const val EXPERT_CMPL = 2

    fun handle(m: Component) {
        val match = Regex(""".(\d+) .(\d+:\d+\.\d+) .(.+)""").find(m.string) ?: return

        val medals = match.groups[1]?.value?.toIntOrNull() ?: return
        val timeElapsedString = match.groups[2]?.value ?: return
        val timeMillis =
            TimeUtil.parseMmSsMmmToTimeUnit(timeElapsedString, TimeUnit.MILLISECONDS)

        // thresholds in milliseconds
        val two = TimeUnit.MINUTES.toMillis(2)
        val three = TimeUnit.MINUTES.toMillis(3)
        val four = TimeUnit.MINUTES.toMillis(4)
        val five = TimeUnit.MINUTES.toMillis(5)

        // helper to apply increments
        fun inc(criteria: QuestCriteria, amount: Int = 1, tagSuffix: String? = null) {
            val tag = tagSuffix ?: "increment_${criteria.name.lowercase()}"
            QuestStorage.applyIncrement(
                IncrementContext(
                    Game.PARKOUR_WARRIOR_DOJO,
                    criteria,
                    amount,
                    tag
                ),
                true
            )
        }

        // Increment total medals banked
        QuestStorage.applyIncrement(
            IncrementContext(
                Game.PARKOUR_WARRIOR_DOJO,
                QuestCriteria.PW_SOLO_TOTAL_MEDALS_BANKED,
                medals,
                "medals_banked"
            ),
            true
        )

        val runType = when (medals) {
            21 -> EXPERT_CMPL
            in 16..20 -> ADVANCED_CMPL
            else -> STANDARD_CMPL
        }

        // Increment standard run total (all runs are at least standard)
        QuestStorage.applyIncrement(
            IncrementContext(
                Game.PARKOUR_WARRIOR_DOJO,
                QuestCriteria.PW_SOLO_TOTAL_STANDARD_CMPLS,
                1,
                "standard_completion_total"
            ),
            true
        )

        if (runType >= ADVANCED_CMPL && timeMillis <= five) {
            QuestStorage.applyIncrement(
                IncrementContext(
                    Game.PARKOUR_WARRIOR_DOJO,
                    QuestCriteria.PW_SOLO_TOTAL_ADVANCED_CMPLS,
                    1,
                    "advanced_completion_total"
                ),
                true
            )
        }

        if (timeMillis <= two) {
            inc(QuestCriteria.PW_SOLO_STANDARD_CMPL_BELOW_TWO_MIN, 1, "timed_criteria_TWO_MIN")
        }
        if (timeMillis <= three) {
            inc(QuestCriteria.PW_SOLO_STANDARD_CMPL_BELOW_THREE_MIN, 1, "timed_criteria_THREE_MIN")
        }
        if (runType >= ADVANCED_CMPL && timeMillis <= four) {
            inc(QuestCriteria.PW_SOLO_ADVANCED_CMPL_BELOW_FOUR_MIN, 1, "timed_criteria_FOUR_MIN")
        }

        if (timeMillis <= five) {
            val criteria = if (runType == STANDARD_CMPL)
                QuestCriteria.PW_SOLO_STANDARD_CMPL_BELOW_FIVE_MIN
            else
                QuestCriteria.PW_SOLO_ADVANCED_CMPL_BELOW_FIVE_MIN

            inc(criteria, 1, "timed_criteria_FIVE_MIN")
        }
    }

}