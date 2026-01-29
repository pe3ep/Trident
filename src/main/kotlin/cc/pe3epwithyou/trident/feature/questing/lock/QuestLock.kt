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
import com.noxcrew.sheeplib.CompoundWidget
import com.noxcrew.sheeplib.layout.GridLayout
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

    class Widget(x: Int, y: Int) : CompoundWidget(x + 200, y + 60, 52, 52) {
        override val layout = GridLayout(2) {
            val games = Game.entries.toMutableSet()
            games.remove(Game.HUB)
            games.remove(Game.FISHING)
            games.remove(Game.BATTLE_BOX_ARENA)

            var col = 0
            var row = 0
            games.forEach {
                GameWidget(it).at(row, col)
                col++
                if (col == 3) {
                    col = 0
                    row++
                }
            }
        }

        init {
            layout.arrangeElements()
            layout.visitWidgets(this::addChild)
            layout.x = getX()
            layout.y = getY()
        }
    }

    class GameWidget(val game: Game) : AbstractWidget(0, 0, 16, 16, Component.empty()) {
        private val texture: Texture = Texture(
            Resources.mcc("textures/${game.icon}"),
            16,
            16
        )

        override fun renderWidget(
            graphics: GuiGraphics,
            i: Int,
            j: Int,
            f: Float
        ) {
            if (isHovered || game == lockedGame) RaritySlot.renderOutline(
                graphics, x, y, TextColor.fromRgb(
                    when {
                        isHovered -> 0xcecece
                        game == lockedGame -> 0xFFFFFF
                        else -> 0x000000
                    }
                ), DisplayType.OUTLINE
            )
            texture.blit(graphics, x, y)
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