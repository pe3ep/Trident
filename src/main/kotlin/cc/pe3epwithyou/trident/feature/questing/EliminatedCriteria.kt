package cc.pe3epwithyou.trident.feature.questing

import cc.pe3epwithyou.trident.state.Game
import cc.pe3epwithyou.trident.utils.ChatUtils

object EliminatedCriteria {
    fun get(game: Game, amount: Int = 1, sourceTag: String? = null): IncrementContext? {
        val keywords = listOf("PLAYERS_KILLED", "PLAYERS_ELIMINATED", "KILLED", "ELIMINATE")
        val criteria = findForGame(game, keywords)
        return criteria?.let { IncrementContext(game = game, criteria = it, amount = amount, sourceTag = sourceTag) }
    }

    private fun findForGame(game: Game, keywords: List<String>): QuestCriteria? {
        val gameQuestList = mapGameToGameQuests(game)?.list ?: emptyList()

        val upperKeywords = keywords.map { it.uppercase() }

        val matchInGame = gameQuestList.firstOrNull { crit ->
            val name = crit.name.uppercase()
            upperKeywords.any { kw -> name.contains(kw) }
        }
        if (matchInGame != null) return matchInGame

        ChatUtils.error("Failed to find quest criteria for ${game.title}")
        ChatUtils.error("Keywords: $keywords")
        return null
    }

    private fun mapGameToGameQuests(game: Game): GameQuests? {
        if (game.name in GameQuests.entries.map { v -> v.name }) {
            return GameQuests.valueOf(game.name)
        }
        return null
    }
}