package cc.pe3epwithyou.trident.widgets.questing

import cc.pe3epwithyou.trident.client.events.QuestIncrementContext
import cc.pe3epwithyou.trident.state.MCCGame
import cc.pe3epwithyou.trident.utils.ChatUtils

object GenericCompletionCriteria {
    fun playerEliminated(game: MCCGame, amount: Int = 1, sourceTag: String? = null): QuestIncrementContext? {
        val keywords = listOf("PLAYERS_KILLED", "PLAYERS_ELIMINATED", "KILLED", "ELIMINATE")
        val criteria = findForGame(game, keywords)
        return criteria?.let { QuestIncrementContext(game = game, criteria = it, amount = amount, sourceTag = sourceTag) }
    }

    fun topN(game: MCCGame, n: Int, amount: Int = 1, sourceTag: String? = null): QuestIncrementContext? {
        val keywords = when (n) {
            3 -> listOf("TOP_THREE", "TOP_3", "TOP3")
            5 -> listOf("TOP_FIVE", "TOP_5", "TOP5")
            8 -> listOf("TOP_EIGHT", "TOP_8", "TOP8")
            10 -> listOf("TOP_TEN", "TOP_10", "TOP10")
            else -> listOf("TOP_$n", "TOP_${n}_")
        }
        val criteria = findForGame(game, keywords)
        return criteria?.let { QuestIncrementContext(game = game, criteria = it, amount = amount, sourceTag = sourceTag) }
    }

    fun surviveMinutes(game: MCCGame, minutes: Int, amount: Int = 1, sourceTag: String? = null): QuestIncrementContext? {
        val keywords = when (minutes) {
            1 -> listOf("SURVIVE_1M", "SURVIVED_MINUTE", "SURVIVE_60S", "SURVIVE_1M")
            2 -> listOf("SURVIVE_2M", "SURVIVED_TWO_MINUTE", "SURVIVE_2M")
            else -> listOf("SURVIVE_${minutes}M", "SURVIVE_${minutes}_MIN")
        }
        val criteria = findForGame(game, keywords)
        return criteria?.let { QuestIncrementContext(game = game, criteria = it, amount = amount, sourceTag = sourceTag) }
    }

    fun gamesPlayed(game: MCCGame, amount: Int = 1, sourceTag: String? = null): QuestIncrementContext? {
        val keywords = listOf("GAMES_PLAYED", "GAMES", "COMPLETE")
        val criteria = findForGame(game, keywords)
        return criteria?.let { QuestIncrementContext(game = game, criteria = it, amount = amount, sourceTag = sourceTag) }
    }

    fun wins(game: MCCGame, amount: Int = 1, sourceTag: String? = null): QuestIncrementContext? {
        val keywords = listOf("WINS", "WIN")
        val criteria = findForGame(game, keywords)
        return criteria?.let { QuestIncrementContext(game = game, criteria = it, amount = amount, sourceTag = sourceTag) }
    }

    fun punchChickens(game: MCCGame, amount: Int = 1, sourceTag: String? = null): QuestIncrementContext? {
        val keywords = listOf("CHICKENS_PUNCHED", "PUNCH_CHICKENS", "PUNCH")
        val criteria = findForGame(game, keywords)
        return criteria?.let { QuestIncrementContext(game = game, criteria = it, amount = amount, sourceTag = sourceTag) }
    }

    /**
     * Find a CompletionCriteria for the given game by matching any of the provided keywords
     * against the enum name of the criteria within the game's GameQuests list.
     */
    private fun findForGame(game: MCCGame, keywords: List<String>): CompletionCriteria? {
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

    private fun mapGameToGameQuests(game: MCCGame): GameQuests? {
        if (game.name in GameQuests.entries.map { v -> v.name }) {
            return GameQuests.valueOf(game.name)
        }
        return null
    }
}