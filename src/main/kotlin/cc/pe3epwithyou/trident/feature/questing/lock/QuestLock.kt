package cc.pe3epwithyou.trident.feature.questing.lock

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.events.click.ClickEvents
import cc.pe3epwithyou.trident.events.click.ContainerClickContext
import cc.pe3epwithyou.trident.feature.questing.Quest
import cc.pe3epwithyou.trident.feature.rarityslot.DisplayType
import cc.pe3epwithyou.trident.feature.rarityslot.RaritySlot
import cc.pe3epwithyou.trident.mixin.accessors.AbstractContainerScreenAccessor
import cc.pe3epwithyou.trident.state.FontCollection
import cc.pe3epwithyou.trident.state.Game
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.Texture
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.defaultFont
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.mccFont
import cc.pe3epwithyou.trident.utils.minecraft
import cc.pe3epwithyou.trident.utils.playMaster
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextColor
import net.minecraft.world.inventory.Slot
import java.util.function.Consumer

object QuestLock {
    val LOCK_TEXTURE = Texture(
        Resources.trident("textures/interface/upgrade_locked.png"), 7, 7
    )

    var lockedGames: MutableSet<Game> = mutableSetOf()
    val questContainers = mutableMapOf<Int, QuestContainer>(
        37 to QuestContainer(), 39 to QuestContainer(), 41 to QuestContainer()
    )

    fun register() {
        ClickEvents.onClick(::handleClick)
    }

    fun handleClick(ctx: ContainerClickContext) = with(ctx) {
        requireTitle("ISLAND REWARDS")
        if (!Config.Global.questLock) return@with
        val itemName = clickedItem()?.hoverName?.string ?: return@with
        val index = clickedSlot()?.index ?: return@with
        if (index !in questContainers.keys) return@with
        if (!itemName.contains("Quest", ignoreCase = true)) {
            cancel()
            return@with
        }
        val questContainer = questContainers[index] ?: return@with
        if (shift) {
            if (itemName.contains("Scroll", ignoreCase = true) && right) {
                return@with
            }
            if (!questContainer.isUnlocked && (left || right)) {
                cancel()
                minecraft().soundManager.playMaster(
                    Resources.minecraft("block.note_block.bass"),
                    pitch = 0.5f
                )
            }
            return@with
        }
        if (right) {
            if (!questContainer.isUnlocked) {
                setLocked(index, false)
                minecraft().soundManager.playMaster(Resources.mcc("ui.click_normal"))
            }
        }

    }

    @JvmStatic
    fun modifyTooltip(consumer: Consumer<Component>) {
        if (!MCCIState.isOnIsland()) return
        if (!Config.Global.questLock) return
        val screen = minecraft().screen as? ContainerScreen ?: return
        val slot = (screen as AbstractContainerScreenAccessor).hoveredSlot ?: return
        if (slot.index !in questContainers.keys) return
        if (!slot.item.hoverName.string.contains("Quest", ignoreCase = true)) return
        if (questContainers[slot.index]?.isUnlocked == true) return

        val component =
            FontCollection.get("_fonts/icon/click_action_right.png", 7, 7).withColor(0xffffff)
                .mccFont("icon")
                .append(Component.literal(" > ").withStyle(ChatFormatting.DARK_GRAY).defaultFont())
                .append(
                    Component.literal("Right-Click to ").withColor(0xe9d282).defaultFont()
                ).append(
                    Component.literal("Unlock Quest").withColor(0xfbe460).defaultFont()
                )

        consumer.accept(component)
    }

    fun shouldLock(quests: List<Quest>) = quests.any { it.game in lockedGames }

    fun setLocked(slot: Int, bl: Boolean) {
        val questSlot = questContainers[slot] ?: return
        questSlot.isUnlocked = !bl
    }

    @JvmStatic
    fun renderLock(graphics: GuiGraphics, slot: Slot) {
        if (!MCCIState.isOnIsland()) return
        if (!Config.Global.questLock) return
        val screen = minecraft().screen ?: return
        if ("ISLAND REWARDS" !in screen.title.string) return
        if ("Quest" !in slot.item.hoverName.string) return

        val questSlot = questContainers[slot.index] ?: return
        if (!questSlot.isUnlocked && questSlot.quests.any { it.slot == slot.index && it.game in lockedGames }) {
            RaritySlot.renderOutline(
                graphics, slot.x, slot.y, TextColor.fromRgb(0xef3f15), DisplayType.OUTLINE
            )
            LOCK_TEXTURE.blit(graphics, slot.x + 4, slot.y - 3)
        }
    }

    data class QuestContainer(
        var quests: List<Quest> = emptyList(),
        var isUnlocked: Boolean = false,
    )
}