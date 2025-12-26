package cc.pe3epwithyou.trident.feature.questing.game

import cc.pe3epwithyou.trident.feature.questing.IncrementContext
import cc.pe3epwithyou.trident.feature.questing.QuestCriteria
import cc.pe3epwithyou.trident.feature.questing.QuestListener
import cc.pe3epwithyou.trident.feature.questing.QuestStorage
import cc.pe3epwithyou.trident.mixin.GuiAccessor
import cc.pe3epwithyou.trident.state.Game
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component

object DynaballHandlers {
    fun scheduleDynaball() {
        QuestListener.handleTimedQuest(1L, true) {
            QuestStorage.applyIncrement(
                IncrementContext(
                    Game.DYNABALL,
                    QuestCriteria.DYNABALL_SURVIVE_1M,
                    1,
                    "dynaball_survived_60s"
                )
            )
        }
        QuestListener.handleTimedQuest(2L, true) {
            QuestStorage.applyIncrement(
                IncrementContext(
                    Game.DYNABALL,
                    QuestCriteria.DYNABALL_SURVIVE_2M,
                    1,
                    "dynaball_survived_2m"
                )
            )
        }
        QuestListener.handleTimedQuest(4L, true) {
            QuestStorage.applyIncrement(
                IncrementContext(
                    Game.DYNABALL,
                    QuestCriteria.DYNABALL_SURVIVE_4M,
                    1,
                    "dynaball_survived_4m"
                )
            )
        }
    }

    fun handle(m: Component) {
        Regex("^\\[.] Game Over!").find(m.string)?.let {
            val title = (Minecraft.getInstance().gui as GuiAccessor).title ?: return
            if (title.string != "Victory!") return

            QuestStorage.applyIncrement(
                IncrementContext(
                    Game.DYNABALL,
                    QuestCriteria.DYNABALL_WINS,
                    1,
                    "dynaball_wins"
                )
            )
        }

    }
}