package cc.pe3epwithyou.trident.utils

import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.world.scores.DisplaySlot
import net.minecraft.world.scores.Objective
import net.minecraft.world.scores.Scoreboard

object ScoreboardUtils {
    fun findInScoreboard(predicate: Regex): MatchResult? {
        val c = getLines().find { component ->
            predicate.containsMatchIn(component.string)
        } ?: return null
        return predicate.find(c.string)
    }

    fun getLines(): List<Component> {
        getScoreboard()?.let { scoreboard ->
            val obj = getObjective() ?: return emptyList()
            return scoreboard.listPlayerScores(obj).mapNotNull { it.display }
        }
        return emptyList()
    }

    private fun getObjective(): Objective? {
        return getScoreboard()?.getDisplayObjective(DisplaySlot.SIDEBAR)
    }

    private fun getScoreboard(): Scoreboard? {
        val level = Minecraft.getInstance().level ?: return null
        return level.scoreboard
    }
}