package cc.pe3epwithyou.trident.feature.questing

import cc.pe3epwithyou.trident.interfaces.DialogCollection
import cc.pe3epwithyou.trident.state.Game
import cc.pe3epwithyou.trident.utils.ChatUtils
import cc.pe3epwithyou.trident.utils.WorldUtils
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

object QuestStorage {
    private val store: MutableMap<Game, MutableList<Quest>> = ConcurrentHashMap()
    private val lastOperation: MutableMap<QuestCriteria, UUID> = ConcurrentHashMap()
    var dailyRemaining: Int = 0
    var weeklyRemaining: Int = 0

    init {
        for (g in Game.entries) store[g] = CopyOnWriteArrayList()
    }

    fun getActiveQuests(game: Game): List<Quest> = store[game]?.toList() ?: emptyList()

    /**
     * Clear all current stored quests and replace them with the provided list.
     * Each quest will be added to the list for its `quest.game`.
     *
     * This is suitable for loading quests from disk / network or refreshing
     * the active set wholesale.
     */
    fun loadQuests(quests: List<Quest>) {
        // Clear each game's list (preserves map keys)
        for (g in Game.entries) {
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
    fun applyIncrement(ctx: IncrementContext, canBeDuplicated: Boolean = false): Boolean {
        ChatUtils.debugLog("Received increment from context ${ctx.sourceTag}: amount: ${ctx.amount}")
        if (QuestListener.checkIfPlobby()) return false
        val id = WorldUtils.getGameID()
        if (!canBeDuplicated && lastOperation[ctx.criteria] == id) {
            ChatUtils.warn("Got duplicate context increment by ${ctx.amount} from ${ctx.sourceTag}")
            return false
        }
        val quests = store[ctx.game] ?: return false
        var updated = false
        for (q in quests) {
            if (q.criteria == ctx.criteria && !q.isCompleted) {
                q.increment(ctx.amount)
                /** Save the last operation to avoid duplicates */
                if (!canBeDuplicated) lastOperation[ctx.criteria] = id
                updated = true
            }
        }
        if (updated) DialogCollection.refreshDialog("questing")
        return updated
    }
}