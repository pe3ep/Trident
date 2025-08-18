package cc.pe3epwithyou.trident.client.events.questing

import cc.pe3epwithyou.trident.client.events.QuestIncrementContext
import cc.pe3epwithyou.trident.mixin.GuiAccessor
import cc.pe3epwithyou.trident.state.MCCGame
import cc.pe3epwithyou.trident.widgets.questing.CompletionCriteria
import cc.pe3epwithyou.trident.widgets.questing.QuestStorage
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component

object TGTTOSQuestEvents {
    // helper to apply increments
    private fun inc(criteria: CompletionCriteria, tagSuffix: String? = null, canBeDuplicated: Boolean = false) {
        val tag = tagSuffix ?: "increment_${criteria.name.lowercase()}"
        QuestStorage.applyIncrement(
            QuestIncrementContext(
                MCCGame.PARKOUR_WARRIOR_DOJO,
                criteria,
                1,
                tag
            ),
            canBeDuplicated
        )
    }

    fun handleTGTTOS(m: Component) {
        handleRoundPlacement(m)
        val gameOver = Regex("^\\[.] Game Over!").find(m.string)
        if (gameOver != null) {
            val subtitle = (Minecraft.getInstance().gui as GuiAccessor).subtitle ?: return
            val match = Regex("(\\d+)(st|nd|rd|th) Place!").find(subtitle.string) ?: return
            val placement = match.groups[1]?.value?.toIntOrNull() ?: return

            if (placement <= 8) {
                inc(CompletionCriteria.TGTTOS_TOP_EIGHT, "tgttos_top8")
            }
            if (placement <= 5) {
                inc(CompletionCriteria.TGTTOS_TOP_FIVE, "tgttos_top5")
            }
            if (placement <= 3) {
                inc(CompletionCriteria.TGTTOS_TOP_THREE, "tgttos_top3")
            }
        }
    }

    private fun handleRoundPlacement(m: Component) {
        val match =
            Regex("^\\[.] .+, you finished the round and came in (\\d+)(st|nd|rd|th) place!")
                .find(m.string) ?: return
        val placement = match.groups[1]?.value?.toInt() ?: return

        inc(CompletionCriteria.TGTTOS_CHICKENS_PUNCHED, "tgttos_chickens_punched", true)

        if (placement <= 8) {
            inc(CompletionCriteria.TGTTOS_ROUND_TOP_EIGHT, "tgttos_round_top8", true)
        }
        if (placement <= 5) {
            inc(CompletionCriteria.TGTTOS_ROUND_TOP_FIVE, "tgttos_round_top5", true)
        }
        if (placement <= 3) {
            inc(CompletionCriteria.TGTTOS_ROUND_TOP_THREE, "tgttos_round_top3", true)
        }
    }
}