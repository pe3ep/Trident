package cc.pe3epwithyou.trident.feature.questing.lock

import cc.pe3epwithyou.trident.events.container.withContainerCtx
import cc.pe3epwithyou.trident.feature.questing.QuestListener
import cc.pe3epwithyou.trident.state.Game
import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.Texture
import cc.pe3epwithyou.trident.utils.extensions.GraphicsExtensions.fillRoundedAll
import cc.pe3epwithyou.trident.utils.minecraft
import com.noxcrew.sheeplib.util.opacity
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.client.sounds.SoundManager
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvent
import java.util.*

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
        val isLocked = game in QuestLock.lockedGames
        val color = when {
            isLocked -> 0xFFFFFF opacity 96
            isHovered -> 0xFFFFFF opacity 32
            else -> 0xFFFFFF opacity 0
        }
        graphics.fillRoundedAll(x, y, 14, 14, color)
        texture.blit(graphics, x + 1, y + 1)
        if (isLocked) QuestLock.LOCK_TEXTURE.blit(graphics, x - 2, y - 2)
    }

    override fun onClick(mouseButtonEvent: MouseButtonEvent, bl: Boolean) {
        if (game in QuestLock.lockedGames) {
            QuestLock.lockedGames.remove(game)
            return
        }
        QuestLock.lockedGames.add(game)
        val screen = minecraft().screen as? ContainerScreen ?: return
        withContainerCtx(screen) {
            QuestListener.findQuests(this)
        }
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