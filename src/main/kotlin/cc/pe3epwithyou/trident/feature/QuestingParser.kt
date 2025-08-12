package cc.pe3epwithyou.trident.feature

import cc.pe3epwithyou.trident.utils.ChatUtils
import net.minecraft.core.component.DataComponents
import net.minecraft.world.inventory.Slot

object QuestingParser {
    fun parseSlot(slot: Slot) {
        val item = slot.item
        val model = item.get(DataComponents.ITEM_MODEL)
        if (model == null) {
            ChatUtils.error("Failed to parse questing item: Missing model")
            return
        }
        ChatUtils.sendMessage(model.path)
    }
}