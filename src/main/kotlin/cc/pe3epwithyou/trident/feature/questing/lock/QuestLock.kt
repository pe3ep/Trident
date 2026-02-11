package cc.pe3epwithyou.trident.feature.questing.lock

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.events.click.ClickEvents
import cc.pe3epwithyou.trident.events.click.ContainerClickContext
import cc.pe3epwithyou.trident.feature.questing.Quest
import cc.pe3epwithyou.trident.feature.rarityslot.DisplayType
import cc.pe3epwithyou.trident.feature.rarityslot.RaritySlot
import cc.pe3epwithyou.trident.state.Game
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.utils.*
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.TextColor
import net.minecraft.world.inventory.Slot

object QuestLock {
    val LOCK_TEXTURE = Texture(
        Resources.trident("textures/interface/upgrade_locked.png"), 7, 7
    )

    var lockedGames: MutableSet<Game> = mutableSetOf()
    val questSlots = mutableMapOf<Int, QuestSlot>(
        37 to QuestSlot(),
        39 to QuestSlot(),
        41 to QuestSlot()
    )

    fun register() {
        ClickEvents.onClick(::handleClick)
    }

    fun handleClick(ctx: ContainerClickContext) = with(ctx) {
        requireTitle("ISLAND REWARDS")
        if (!Config.Global.questLock) return@with

        val itemName = clickedItem()?.hoverName?.string ?: return@with
        val index = clickedSlot()?.index ?: return@with

        if (index !in questSlots) return@with
        if (itemName.contains("Quest", ignoreCase = true)) {
            cancel()
            return@with
        }

        val questSlot = questSlots[index] ?: return@with
        val quests = questSlot.quests
        if (quests.any { it.slot == index && it.game in lockedGames }) {
            if (!questSlot.isLocked) return@with
            Logger.debugLog("Quest slot $index is locked")
            cancel()
            if (!questSlot.isWaitingForUnlock) {
                questSlot.isWaitingForUnlock = true
                DelayedAction.delay(1_500) {
                    setLocked(index, false)
                }
            }
        }
    }

    fun shouldLock(quests: List<Quest>) = quests.any { it.game in lockedGames }

    fun setLocked(slot: Int, bl: Boolean) {
        val questSlot = questSlots[slot] ?: return
        questSlot.isLocked = bl
        if (!bl) questSlot.isWaitingForUnlock = false
    }

    @JvmStatic
    fun renderLock(graphics: GuiGraphics, slot: Slot) {
        if (!MCCIState.isOnIsland()) return
        if (!Config.Global.questLock) return

        val screen = minecraft().screen ?: return
        if ("ISLAND REWARDS" !in screen.title.string) return
        if ("Quest" !in slot.item.hoverName.string) return

        val questSlot = questSlots[slot.index] ?: return
        if (questSlot.isLocked && questSlot.quests.any { it.slot == slot.index && it.game in lockedGames }) {
            RaritySlot.renderOutline(
                graphics, slot.x, slot.y, TextColor.fromRgb(
                    when {
                        questSlot.isWaitingForUnlock -> 0xffc600
                        else -> 0xef3f15
                    }
                ), DisplayType.OUTLINE
            )
            LOCK_TEXTURE.blit(graphics, slot.x + 4, slot.y - 3)
        }
    }

    data class QuestSlot(
        var quests: List<Quest> = emptyList(),
        var isWaitingForUnlock: Boolean = false,
        var isLocked: Boolean = false
    )
}