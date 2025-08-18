package cc.pe3epwithyou.trident.client.events.questing

import cc.pe3epwithyou.trident.client.events.QuestIncrementContext
import cc.pe3epwithyou.trident.state.MCCGame
import cc.pe3epwithyou.trident.widgets.questing.CompletionCriteria
import cc.pe3epwithyou.trident.widgets.questing.QuestStorage
import net.minecraft.network.chat.Component

object SurvivorQuestEvents {
    fun handlePKWS(m: Component) {
        val leapFinished = Regex("^\\[.] Leap (\\d) complete in: .+").find(m.string)
        if (leapFinished != null) {
            val group = leapFinished.groups[1]   ?: return
            val leap = group.value.toIntOrNull() ?: return
            val criteria = when (leap) {
                2 -> CompletionCriteria.PW_SURVIVAL_LEAP_2_COMPLETION
                4 -> CompletionCriteria.PW_SURVIVAL_LEAP_4_COMPLETION
                6 -> CompletionCriteria.PW_SURVIVAL_LEAP_6_COMPLETION
                else -> return
            }
            val ctx = QuestIncrementContext(
                MCCGame.PARKOUR_WARRIOR_SURVIVOR,
                criteria,
                1,
                "leap_finished"
            )
            QuestStorage.applyIncrement(ctx)
        }

        val obstacleComplete = Regex("^\\[.] Leap \\d - \\d: .+ complete!").find(m.string)
        if (obstacleComplete != null) {
            val ctx = QuestIncrementContext(
                MCCGame.PARKOUR_WARRIOR_SURVIVOR,
                CompletionCriteria.PW_SURVIVAL_OBSTACLES_COMPLETED,
                1,
                "obstacle_complete"
            )
            QuestStorage.applyIncrement(ctx)
        }

        val playerOutlived = Regex("^\\[.] .+ was eliminated\\. \\[.+]").find(m.string)
        if (playerOutlived != null) {
            val ctx = QuestIncrementContext(
                MCCGame.PARKOUR_WARRIOR_SURVIVOR,
                CompletionCriteria.PW_SURVIVAL_PLAYERS_ELIMINATED,
                1,
                "player_outlived"
            )
            QuestStorage.applyIncrement(ctx)
        }
    }

}