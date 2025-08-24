package cc.pe3epwithyou.trident.feature.questing.game

import cc.pe3epwithyou.trident.feature.questing.IncrementContext
import cc.pe3epwithyou.trident.feature.questing.QuestCriteria
import cc.pe3epwithyou.trident.feature.questing.QuestListener
import cc.pe3epwithyou.trident.feature.questing.QuestStorage
import cc.pe3epwithyou.trident.mixin.GuiAccessor
import cc.pe3epwithyou.trident.state.MCCGame
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component

object SkyBattleHandlers {
    private fun inc(criteria: QuestCriteria, tagSuffix: String? = null, canBeDuplicated: Boolean = false) {
        val tag = tagSuffix ?: "increment_${criteria.name.lowercase()}"
        QuestStorage.applyIncrement(
            IncrementContext(
                MCCGame.SKY_BATTLE,
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
                    MCCGame.SKY_BATTLE,
                    QuestCriteria.SKY_BATTLE_QUADS_SURVIVED_MINUTE,
                    1,
                    "skb_survived_60s"
                )
            )
        }
    }

    fun scheduleSurvivedTwoMinutes() {
        QuestListener.handleTimedQuest(2L, true) {
            QuestStorage.applyIncrement(
                IncrementContext(
                    MCCGame.SKY_BATTLE,
                    QuestCriteria.SKY_BATTLE_QUADS_SURVIVED_TWO_MINUTE,
                    1,
                    "skb_survived_2m"
                )
            )
        }
    }

    fun handle(m: Component) {
        val eliminated = Regex("^\\[.] Game Over!").find(m.string)
        if (eliminated != null) {
            val subtitle = (Minecraft.getInstance().gui as GuiAccessor).subtitle ?: return
            val match = Regex("(\\d+)(st|nd|rd|th) survivor!").find(subtitle.string) ?: return
            val placement = match.groups[1]?.value?.toIntOrNull() ?: return

            if (placement <= 10) {
                inc(QuestCriteria.SKY_BATTLE_QUADS_SURVIVAL_TOP_TEN, "skb_top10")
            }
            if (placement <= 5) {
                inc(QuestCriteria.SKY_BATTLE_QUADS_SURVIVAL_TOP_FIVE, "skb_top5")
            }
            if (placement <= 3) {
                inc(QuestCriteria.SKY_BATTLE_QUADS_SURVIVAL_TOP_THREE, "skb_top3")
            }
        }
    }
}