package cc.pe3epwithyou.trident.feature.questing

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.interfaces.DialogCollection
import cc.pe3epwithyou.trident.state.Game
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.utils.Logger
import java.util.concurrent.CopyOnWriteArrayList

object QuestStorage {
    private val store: MutableList<QuestHolder> = CopyOnWriteArrayList()

    var dailyRemaining: Int = 0
    var weeklyRemaining: Int = 0

    fun getActiveQuestHolders(game: Game): List<QuestHolder> =
        store.filter { holder -> holder.quests.any { it.game == game } }

    fun getActiveQuests(game: Game): List<Quest> =
        getActiveQuestHolders(game).flatMap { holder ->
            holder.quests.filter { it.game == game }
        }

    fun loadQuests(quests: List<Quest>) {
        store.clear()
        store.addAll(quests.chunked(3).map {
            QuestHolder.create(it)
        })
        if (!Config.Questing.enabled) return
        DialogCollection.refreshDialog("questing")
    }

    fun applyIncrement(ctx: IncrementContext): Boolean {
        Logger.debugLog(
            "Received increment from context ${ctx.sourceTag}, criteria: ${ctx.criteria}: amount: ${ctx.amount}"
        )
        if (MCCIState.isPlobbyGame) return false

        var updated = false

        for (holder in store) {
            for (quest in holder.quests) {
                if (quest.game == ctx.game && quest.criteria == ctx.criteria && !quest.isCompleted) {
                    quest.increment(ctx.amount)
                    updated = true
                }
            }
        }

        if (updated) DialogCollection.refreshDialog("questing")
        return updated
    }
}