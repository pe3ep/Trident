package cc.pe3epwithyou.trident.feature.questing.lock

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.questing.Quest
import cc.pe3epwithyou.trident.feature.questing.QuestListener
import cc.pe3epwithyou.trident.feature.rarityslot.DisplayType
import cc.pe3epwithyou.trident.feature.rarityslot.RaritySlot
import cc.pe3epwithyou.trident.state.Game
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.utils.*
import cc.pe3epwithyou.trident.utils.extensions.GraphicsExtensions.fillRoundedAll
import com.noxcrew.sheeplib.CompoundWidget
import com.noxcrew.sheeplib.util.opacity
import com.noxcrew.sheeplib.util.opaqueColor
import net.minecraft.ChatFormatting
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

    var lockedGames: MutableSet<Game> = mutableSetOf()
    val questSlots = mutableMapOf<Int, QuestSlot>(
        37 to QuestSlot(),
        39 to QuestSlot(),
        41 to QuestSlot()
    )

    @JvmStatic
    fun handleClick(slot: Slot?, cir: CallbackInfoReturnable<Boolean>) {
        if (!MCCIState.isOnIsland()) return
        if (!Config.Global.questLock) return

        if (slot == null) return
        val screen = minecraft().screen ?: return
        if ("ISLAND REWARDS" !in screen.title.string) return
        if (slot.index !in questSlots) return
        if ("Quest" !in slot.item.hoverName.string) {
            cir.cancel()
            return
        }
        val questSlot = questSlots[slot.index] ?: return
        val quests = questSlot.quests
        if (quests.any { it.slot == slot.index && it.game in lockedGames }) {
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
            val x = graphics.guiWidth() / 2

            var c =
                Component.literal("${lockedGames.size} Game${if (lockedGames.size == 1) "" else "s"} Locked")
                    .withStyle(
                        ChatFormatting.WHITE
                    )

            if (lockedGames.isEmpty()) {
                c = Component.literal("Select Game to Lock").withStyle(ChatFormatting.GRAY)
            }

            graphics.drawCenteredString(
                minecraft().font,
                c,
                x,
                y + 20,
                0xffffff.opaqueColor()
            )
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
            16, 16
        )

        override fun renderWidget(
            graphics: GuiGraphics,
            i: Int,
            j: Int,
            f: Float
        ) {
            val isLocked = game in lockedGames
            val color = when {
                isLocked -> 0xFFFFFF opacity 96
                isHovered -> 0xFFFFFF opacity 32
                else -> 0xFFFFFF opacity 0
            }
            graphics.fillRoundedAll(x, y, 14, 14, color)
            texture.blit(graphics, x + 1, y + 1)
            if (isLocked) LOCK_TEXTURE.blit(graphics, x - 2, y - 2)
        }

        override fun onClick(mouseButtonEvent: MouseButtonEvent, bl: Boolean) {
            if (game in lockedGames) {
                lockedGames.remove(game)
                return
            }
            lockedGames.add(game)
            val screen = minecraft().screen as? ContainerScreen ?: return
            QuestListener.findQuests(screen.context())
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