package cc.pe3epwithyou.trident.widgets.questing

import cc.pe3epwithyou.trident.client.events.QuestIncrementContext
import cc.pe3epwithyou.trident.dialogs.DialogCollection
import cc.pe3epwithyou.trident.state.MCCGame
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

object QuestStorage {
    private val store: MutableMap<MCCGame, MutableList<Quest>> = ConcurrentHashMap()

    init {
        for (g in MCCGame.entries) store[g] = CopyOnWriteArrayList()
    }

    fun getActiveQuests(game: MCCGame): List<Quest> = store[game]?.toList() ?: emptyList()

    fun setActiveQuests(game: MCCGame, quests: List<Quest>) {
        store[game]?.apply {
            clear()
            addAll(quests)
        } ?: store.put(game, CopyOnWriteArrayList(quests))
    }

    /**
     * Apply the increment described by ctx to all matching quests for the given game.
     * Returns true if any quest was updated.
     */
    fun applyIncrement(ctx: QuestIncrementContext): Boolean {
        val quests = store[ctx.game] ?: return false
        var updated = false
        for (q in quests) {
            if (q.criteria == ctx.criteria && !q.isCompleted) {
                q.increment(ctx.amount)
                updated = true
            }
        }
        if (updated) DialogCollection.refreshDialog("questing")
        return updated
    }
}