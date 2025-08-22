package cc.pe3epwithyou.trident.feature

import cc.pe3epwithyou.trident.state.MCCGame
import cc.pe3epwithyou.trident.state.Rarity
import cc.pe3epwithyou.trident.utils.ChatUtils
import cc.pe3epwithyou.trident.utils.ItemParser.getItemLore
import cc.pe3epwithyou.trident.widgets.questing.*
import net.minecraft.core.component.DataComponents
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

object QuestingParser {
    private const val FINISHED_MAPS = "island_interface/generic/clock_used"
    private const val ADD_SCROLL = "island_interface/generic/add"
    private const val COMPLETED_QUEST = "island_interface/quest_log/quest_glow"

    fun parseSlot(slot: Slot): List<Quest>? {
        val item = slot.item
        val model = item.get(DataComponents.ITEM_MODEL)
        if (model == null) {
            ChatUtils.error("Failed to parse questing item in slot ${slot.index}: Missing model")
            return null
        }
        ChatUtils.debugLog("Quest found: Slot ${slot.index}")
        ChatUtils.debugLog("Model: ${model.path}")
        if (model.path == FINISHED_MAPS ||
            model.path == ADD_SCROLL ||
            model.path == COMPLETED_QUEST) {
            return null
        }

        val rarity = when (model.path.split("/").last()) {
            "common"    -> Rarity.COMMON
            "uncommon"  -> Rarity.UNCOMMON
            "rare"      -> Rarity.RARE
            "epic"      -> Rarity.EPIC
            "legendary" -> Rarity.LEGENDARY
            "mythic"    -> Rarity.MYTHIC
            else -> Rarity.COMMON
        }
        ChatUtils.debugLog("Got rarity - $rarity")

        var type = QuestType.DEFAULT
        for (t in QuestType.entries) {
            if (t.directoryPath in model.path) {
                type = t
                break
            }
        }
        ChatUtils.debugLog("Got type - $type")

        val subtype = when {
            "Daily" in item.hoverName.string -> QuestSubtype.DAILY
            "Weekly" in item.hoverName.string -> QuestSubtype.WEEKLY
            "Scroll" in item.hoverName.string -> QuestSubtype.SCROLL
            else -> return null
        }
        ChatUtils.debugLog("Got subtype - $subtype")

        val quests = mutableListOf<Quest>()
        val loreResult = parseLore(slot.item)
        ChatUtils.debugLog("Got parsed quests - $loreResult")
        if (loreResult.isEmpty()) return null
        loreResult.forEach { parsedQuest ->
            quests.add(
                Quest(
                    parsedQuest.game,
                    type,
                    subtype,
                    rarity,
                    parsedQuest.criteria,
                    parsedQuest.progress.first,
                    parsedQuest.progress.second
                )
            )
        }
        return quests
    }

    private fun parseLore(item: ItemStack): List<ParsedQuest> {
        val lines = item.getItemLore()

        val parsedQuests = mutableListOf<ParsedQuest>()

        var tempGame: MCCGame? = null
        var tempCriteria: CompletionCriteria? = null
        var tempProgress: Pair<Int, Int>
        var tempQuestString = ""
        lines.forEachIndexed { index, l ->
            if (index <= 4) return@forEachIndexed
            if (parsedQuests.size >= 3) return@forEachIndexed

            if ("Progress: " in l.string) {
                val t = l.string.split(": ")[1]
                val current = t.split("/")[0].replace(",", "").toInt()
                val total = t.split("/")[1].replace(",", "").toInt()
                tempProgress = Pair(current, total)
                ChatUtils.debugLog("Got progress -> $tempProgress")
                val q = ParsedQuest(
                    tempGame ?: return@forEachIndexed,
                    tempCriteria ?: return@forEachIndexed,
                    tempProgress
                )
                parsedQuests.add(q)

                tempGame = null
                tempCriteria = null
                ChatUtils.debugLog("Cleared temp data")
                ChatUtils.debugLog("--------------------")
                return@forEachIndexed
            } else if ("%" in l.string) {
                ChatUtils.debugLog("Hit the %, trying to detect game quest")
                tempGame = getQuestGame(tempQuestString) ?: return@forEachIndexed
                ChatUtils.debugLog("Detected game quest -> ${tempGame.title}: $tempQuestString")
                val criteriaList = GameQuests.valueOf(tempGame.name).list
                ChatUtils.debugLog("Criteria list -> $criteriaList")
                for (crit in criteriaList) {
                    if (!tempQuestString.contains(crit.regexPattern)) continue
                    ChatUtils.debugLog("Detected game criteria -> $crit")
                    tempCriteria = crit
                }
                tempQuestString = ""
            } else {
                val suffix = if (l.string.endsWith(" ")) "" else " "
                tempQuestString += (l.string + suffix).replace(",", "")
                ChatUtils.debugLog("New tempQuestString: $tempQuestString")
            }
        }

        return parsedQuests
    }

    private data class ParsedQuest(val game: MCCGame, val criteria: CompletionCriteria, val progress: Pair<Int, Int>)

    private fun getQuestGame(text: String): MCCGame? {
        MCCGame.entries.forEach { g ->
            if (g.title in text) return g
        }
        return null
    }
}