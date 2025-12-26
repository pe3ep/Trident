package cc.pe3epwithyou.trident.feature.questing.game

import cc.pe3epwithyou.trident.feature.questing.IncrementContext
import cc.pe3epwithyou.trident.feature.questing.QuestCriteria
import cc.pe3epwithyou.trident.feature.questing.QuestStorage
import cc.pe3epwithyou.trident.mixin.GuiAccessor
import cc.pe3epwithyou.trident.state.Game
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component

object TGTTOSHandlers {
    // helper to apply increments
    private fun inc(criteria: QuestCriteria, tagSuffix: String? = null, canBeDuplicated: Boolean = false) {
        val tag = tagSuffix ?: "increment_${criteria.name.lowercase()}"
        QuestStorage.applyIncrement(
            IncrementContext(
                Game.TGTTOS,
                criteria,
                1,
                tag
            ),
            canBeDuplicated
        )
    }

    fun handle(m: Component) {
        handleRoundPlacement(m)
        Regex("^\\[.] Game Over!").find(m.string)?.let {
            val subtitle = (Minecraft.getInstance().gui as GuiAccessor).subtitle ?: return
            val match = Regex("(\\d+)(st|nd|rd|th) Place!").find(subtitle.string) ?: return
            val placement = match.groups[1]?.value?.toIntOrNull() ?: return

            if (placement <= 8) {
                inc(QuestCriteria.TGTTOS_TOP_EIGHT, "tgttos_top8")
            }
            if (placement <= 5) {
                inc(QuestCriteria.TGTTOS_TOP_FIVE, "tgttos_top5")
            }
            if (placement <= 3) {
                inc(QuestCriteria.TGTTOS_TOP_THREE, "tgttos_top3")
            }
        }

    }

    private fun handleRoundPlacement(m: Component) {
        val match =
            Regex("^\\[.] .+, you finished the round and came in (\\d+)(st|nd|rd|th) place!")
                .find(m.string) ?: return
        val placement = match.groups[1]?.value?.toIntOrNull() ?: return

        inc(QuestCriteria.TGTTOS_CHICKENS_PUNCHED, "tgttos_chickens_punched", true)

        if (placement <= 8) {
            inc(QuestCriteria.TGTTOS_ROUND_TOP_EIGHT, "tgttos_round_top8", true)
        }
        if (placement <= 5) {
            inc(QuestCriteria.TGTTOS_ROUND_TOP_FIVE, "tgttos_round_top5", true)
        }
        if (placement <= 3) {
            inc(QuestCriteria.TGTTOS_ROUND_TOP_THREE, "tgttos_round_top3", true)
        }
    }
}