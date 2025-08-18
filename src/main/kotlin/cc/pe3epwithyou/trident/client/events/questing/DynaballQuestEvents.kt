package cc.pe3epwithyou.trident.client.events.questing

import cc.pe3epwithyou.trident.client.events.QuestIncrementContext
import cc.pe3epwithyou.trident.mixin.GuiAccessor
import cc.pe3epwithyou.trident.state.MCCGame
import cc.pe3epwithyou.trident.widgets.questing.CompletionCriteria
import cc.pe3epwithyou.trident.widgets.questing.QuestStorage
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component

object DynaballQuestEvents {
    fun scheduleDynaball() {
        QuestListener.handleTimedQuest(1L, true) {
            QuestStorage.applyIncrement(QuestIncrementContext(
                MCCGame.DYNABALL,
                CompletionCriteria.DYNABALL_SURVIVE_1M,
                1,
                "dynaball_survived_60s"
            ))
        }
        QuestListener.handleTimedQuest(2L, true) {
            QuestStorage.applyIncrement(QuestIncrementContext(
                MCCGame.DYNABALL,
                CompletionCriteria.DYNABALL_SURVIVE_2M,
                1,
                "dynaball_survived_2m"
            ))
        }
        QuestListener.handleTimedQuest(4L, true) {
            QuestStorage.applyIncrement(QuestIncrementContext(
                MCCGame.DYNABALL,
                CompletionCriteria.DYNABALL_SURVIVE_4M,
                1,
                "dynaball_survived_4m"
            ))
        }
    }

    fun handleDynaball(m: Component) {
        val eliminated = Regex("^\\[.] Game Over!").find(m.string)
        if (eliminated != null) {
            val title = (Minecraft.getInstance().gui as GuiAccessor).title ?: return
            if (title.string != "Victory!") return

            QuestStorage.applyIncrement(QuestIncrementContext(
                MCCGame.DYNABALL,
                CompletionCriteria.DYNABALL_WINS,
                1,
                "dynaball_wins"
            ))
        }
    }
}