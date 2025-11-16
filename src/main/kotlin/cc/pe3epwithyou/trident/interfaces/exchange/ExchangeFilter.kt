package cc.pe3epwithyou.trident.interfaces.exchange

import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.Texture
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.client.sounds.SoundManager
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvent
import java.util.Optional

class ExchangeFilter(x: Int, y: Int) : AbstractWidget(x, y, 63, 15, Component.empty()) {
    companion object {
        var showOwnedItems: Boolean = true
    }

    init {
        setTooltip(Tooltip.create(Component.literal("Hide Owned Cosmetics")))
    }

    override fun renderWidget(
        graphics: GuiGraphics, i: Int, j: Int, f: Float
    ) {
        Texture(
            Resources.trident("textures/interface/exchange/${if (showOwnedItems) "hide_owned" else "hide_owned_pressed"}.png"),
            63,
            15,
        ).blit(
            graphics, x, y
        )
    }

    override fun onClick(d: Double, e: Double) {
        showOwnedItems = !showOwnedItems
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

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput): Unit = Unit
}