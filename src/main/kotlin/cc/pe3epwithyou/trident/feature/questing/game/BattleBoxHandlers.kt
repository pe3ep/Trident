package cc.pe3epwithyou.trident.feature.questing.game

import cc.pe3epwithyou.trident.feature.questing.IncrementContext
import cc.pe3epwithyou.trident.feature.questing.QuestCriteria
import cc.pe3epwithyou.trident.feature.questing.QuestStorage
import cc.pe3epwithyou.trident.mixin.GuiAccessor
import cc.pe3epwithyou.trident.state.MCCGame
import cc.pe3epwithyou.trident.utils.ChatUtils
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component

object BattleBoxHandlers {
    fun handleBattleBox(m: Component) {
        val roundWon = Regex("\\[.] . .+ Team, you won Round \\d! \\[.+]").find(m.string)
        if (roundWon != null) {
            val ctx = IncrementContext(
                MCCGame.BATTLE_BOX,
                QuestCriteria.BATTLE_BOX_QUADS_TEAM_ROUNDS_WON,
                1,
                "bb_rounds_won"
            )
            QuestStorage.applyIncrement(ctx, true)
        }

        val round3started = Regex("^\\[.] Round 3 started!").find(m.string)
        if (round3started != null) {
            val ctx = IncrementContext(
                MCCGame.BATTLE_BOX,
                QuestCriteria.BATTLE_BOX_QUADS_GAMES_PLAYED,
                1,
                "bb_games_played"
            )
            QuestStorage.applyIncrement(ctx)
        }

        val gameOver = Regex("^\\[.] Game Over!").find(m.string)
        if (gameOver != null) {
            val subtitle = (Minecraft.getInstance().gui as GuiAccessor).subtitle
            if (subtitle == null) {
                ChatUtils.error("Unable to access the subtitle")
                return
            }
            ChatUtils.debugLog("Got subtitle - ${subtitle.string}")
            val match = Regex("Team (\\d)(st|nd|rd|th) Place!").find(subtitle.string) ?: return
            val placement = match.groups[1]?.value?.toIntOrNull() ?: return

            if (placement > 2) return

            val secondPlaceContext = IncrementContext(
                MCCGame.BATTLE_BOX,
                QuestCriteria.BATTLE_BOX_QUADS_TEAM_SECOND_PLACE,
                1,
                "bb_second_place"
            )
            QuestStorage.applyIncrement(secondPlaceContext)

            if (placement != 1) return
            val firstPlaceContext = IncrementContext(
                MCCGame.BATTLE_BOX,
                QuestCriteria.BATTLE_BOX_QUADS_TEAM_FIRST_PLACE,
                1,
                "bb_first_place"
            )
            QuestStorage.applyIncrement(firstPlaceContext)
        }

    }
}