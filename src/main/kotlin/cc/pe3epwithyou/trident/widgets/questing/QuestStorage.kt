package cc.pe3epwithyou.trident.widgets.questing

import cc.pe3epwithyou.trident.client.events.QuestIncrementContext
import cc.pe3epwithyou.trident.dialogs.DialogCollection
import cc.pe3epwithyou.trident.state.MCCGame
import cc.pe3epwithyou.trident.utils.ChatUtils
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
     * Clear all current stored quests and replace them with the provided list.
     * Each quest will be added to the list for its `quest.game`.
     *
     * This is suitable for loading quests from disk / network or refreshing
     * the active set wholesale.
     */
    fun loadQuests(quests: List<Quest>) {
        // Clear each game's list (preserves map keys)
        for (g in MCCGame.entries) {
            store[g]?.clear() ?: store.put(g, CopyOnWriteArrayList())
        }

        // Distribute provided quests into their corresponding game lists
        for (q in quests) {
            val list = store.computeIfAbsent(q.game) { CopyOnWriteArrayList() }
            list.add(q)
        }
        DialogCollection.refreshDialog("questing")
    }

    /**
     * Apply the increment described by ctx to all matching quests for the given game.
     * Returns true if any quest was updated.
     */
    fun applyIncrement(ctx: QuestIncrementContext): Boolean {
        ChatUtils.debugLog("Received increment from context ${ctx.sourceTag}: amount: ${ctx.amount}")
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