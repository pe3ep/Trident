package cc.pe3epwithyou.trident.feature.questing.lock

import cc.pe3epwithyou.trident.client.listeners.ChestScreenListener
import cc.pe3epwithyou.trident.feature.questing.Quest
import cc.pe3epwithyou.trident.feature.rarityslot.DisplayType
import cc.pe3epwithyou.trident.feature.rarityslot.RaritySlot
import cc.pe3epwithyou.trident.state.Game
import cc.pe3epwithyou.trident.utils.DelayedAction
import cc.pe3epwithyou.trident.utils.Logger
import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.Texture
import cc.pe3epwithyou.trident.utils.extensions.GraphicsExtensions.fillRoundedAll
import cc.pe3epwithyou.trident.utils.gridLayout
import com.noxcrew.sheeplib.CompoundWidget
import com.noxcrew.sheeplib.layout.GridLayout
import com.noxcrew.sheeplib.util.opacity
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.client.sounds.SoundManager
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextColor
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.inventory.Slot
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import java.util.*

object QuestLock {
    private val LOCK_TEXTURE = Texture(
        Resources.trident("textures/interface/upgrade_locked.png"), 7, 7
    )

    var lockedGame: Game? = null
    val questSlots = mutableMapOf<Int, QuestSlot>(
        37 to QuestSlot(),
        39 to QuestSlot(),
        41 to QuestSlot()
    )

    @JvmStatic
    fun handleClick(slot: Slot?, cir: CallbackInfoReturnable<Boolean>) {
        if (slot == null) return
        val screen = Minecraft.getInstance().screen ?: return
        if ("ISLAND REWARDS" !in screen.title.string) return
        if (slot.index !in questSlots) return
        if ("Quest" !in slot.item.hoverName.string) {
            cir.cancel()
            return
        }
        val questSlot = questSlots[slot.index] ?: return
        val quests = questSlot.quests
        if (quests.any { it.slot == slot.index && it.game == lockedGame }) {
            if (!questSlot.isLocked) return
            Logger.debugLog("Quest slot ${slot.index} is locked")
            cir.cancel()
            if (!questSlot.isWaitingForUnlock) {
                questSlot.isWaitingForUnlock = true
                DelayedAction.delay(1_500) {
                    setLocked(slot.index, false)
                }
            }
        }
    }

    fun shouldLock(quests: List<Quest>) = quests.any { it.game == lockedGame }

    fun setLocked(slot: Int, bl: Boolean) {
        val questSlot = questSlots[slot] ?: return
        questSlot.isLocked = bl
        if (!bl) questSlot.isWaitingForUnlock = false
    }

    @JvmStatic
    fun renderLock(graphics: GuiGraphics, slot: Slot) {
        val screen = Minecraft.getInstance().screen ?: return
        if ("ISLAND REWARDS" !in screen.title.string) return
        if ("Quest" !in slot.item.hoverName.string) return

        val questSlot = questSlots[slot.index] ?: return
        if (questSlot.isLocked && questSlot.quests.any { it.slot == slot.index && it.game == lockedGame }) {
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

    class Widget(x: Int, y: Int) : CompoundWidget(x, y, 0, 0) {
        override val layout = gridLayout(1, x, y) {
            val games = Game.entries.toMutableSet()
            games.remove(Game.HUB)
            games.remove(Game.FISHING)
            games.remove(Game.BATTLE_BOX_ARENA)

            var col = 0
            games.forEach {
                GameWidget(it).at(0, col)
                col++
            }
        }

        override fun renderWidget(graphics: GuiGraphics, i: Int, j: Int, f: Float) {
            graphics.fillRoundedAll(x - 1, y - 1, width + 2, height + 2, 0x111111 opacity 128)
            super.renderWidget(graphics, i, j, f)
        }

        init {
            layout.arrangeElements()
            layout.visitWidgets(this::addChild)

            width = layout.width
            height = layout.height
        }
    }

    class GameWidget(val game: Game) : AbstractWidget(0, 0, 14, 14, Component.empty()) {
        private val texture: Texture = Texture(
            Resources.mcc("textures/${game.icon}"),
            12,
            12,
            16,16
        )

        override fun renderWidget(
            graphics: GuiGraphics,
            i: Int,
            j: Int,
            f: Float
        ) {
            val color = when {
                lockedGame == game -> 0xFFFFFF opacity 96
                isHovered -> 0xFFFFFF opacity 32
                else -> 0xFFFFFF opacity 0
            }
            graphics.fillRoundedAll(x, y, 14, 14, color)
            texture.blit(graphics, x + 1, y + 1)
        }

        override fun onClick(mouseButtonEvent: MouseButtonEvent, bl: Boolean) {
            if (lockedGame == game) {
                lockedGame = null
                return
            }
            lockedGame = game
            ChestScreenListener.findQuests(Minecraft.getInstance().screen as? ContainerScreen ?: return)
        }

        override fun playDownSound(soundManager: SoundManager) {
            soundManager.play(
                SimpleSoundInstance.forUI(
                    SoundEvent(Resources.mcc("ui.toggle_slide"), Optional.empty()),
                    1.0f,
                    1.0f
                )
            )
        }

        override fun isFocused() = false

        override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) = Unit

    }
}