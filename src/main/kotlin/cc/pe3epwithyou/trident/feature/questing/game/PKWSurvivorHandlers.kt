package cc.pe3epwithyou.trident.feature.questing.game

import cc.pe3epwithyou.trident.feature.questing.IncrementContext
import cc.pe3epwithyou.trident.feature.questing.QuestCriteria
import cc.pe3epwithyou.trident.feature.questing.QuestStorage
import cc.pe3epwithyou.trident.state.MCCGame
import net.minecraft.network.chat.Component

object PKWSurvivorHandlers {
    fun handlePKWS(m: Component) {
        val leapFinished = Regex("^\\[.] Leap (\\d) complete in: .+").find(m.string)
        if (leapFinished != null) {
            val group = leapFinished.groups[1]   ?: return
            val leap = group.value.toIntOrNull() ?: return
            val criteria = when (leap) {
                2 -> QuestCriteria.PW_SURVIVAL_LEAP_2_COMPLETION
                4 -> QuestCriteria.PW_SURVIVAL_LEAP_4_COMPLETION
                6 -> QuestCriteria.PW_SURVIVAL_LEAP_6_COMPLETION
                else -> return
            }
            val ctx = IncrementContext(
                MCCGame.PARKOUR_WARRIOR_SURVIVOR,
                criteria,
                1,
                "leap_finished"
            )
            QuestStorage.applyIncrement(ctx, true)
        }

        val obstacleComplete = Regex("^\\[.] Leap \\d - \\d: .+ complete!").find(m.string)
        if (obstacleComplete != null) {
            val ctx = IncrementContext(
                MCCGame.PARKOUR_WARRIOR_SURVIVOR,
                QuestCriteria.PW_SURVIVAL_OBSTACLES_COMPLETED,
                1,
                "obstacle_complete"
            )
            QuestStorage.applyIncrement(ctx, true)
        }

        val playerOutlived = Regex("^\\[.] .+ was eliminated\\. \\[.+]").find(m.string)
        if (playerOutlived != null) {
            val ctx = IncrementContext(
                MCCGame.PARKOUR_WARRIOR_SURVIVOR,
                QuestCriteria.PW_SURVIVAL_PLAYERS_ELIMINATED,
                1,
                "player_outlived"
            )
            QuestStorage.applyIncrement(ctx, true)
        }
    }

}