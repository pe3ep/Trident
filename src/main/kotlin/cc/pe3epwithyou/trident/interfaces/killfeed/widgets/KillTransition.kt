package cc.pe3epwithyou.trident.interfaces.killfeed.widgets

import cc.pe3epwithyou.trident.utils.Resources
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component

class KillTransition(
    private val leftColor: Int,
    private val rightColor: Int,
) : AbstractWidget(0, 0, 8, 15, Component.empty()) {
    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        val leftPath = Resources.trident("interface/killfeed/left")
        val rightPath = Resources.trident("interface/killfeed/right")
        guiGraphics.blitSprite(
            RenderPipelines.GUI_TEXTURED,
            leftPath,
            x,
            y,
            8,
            15,
            leftColor
        )
        guiGraphics.blitSprite(
            RenderPipelines.GUI_TEXTURED,
            rightPath,
            x,
            y,
            8,
            15,
            rightColor
        )
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) = Unit

}