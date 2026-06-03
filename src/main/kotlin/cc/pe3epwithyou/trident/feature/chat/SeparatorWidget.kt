package cc.pe3epwithyou.trident.feature.chat

import com.noxcrew.sheeplib.util.opacity
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component

class SeparatorWidget : AbstractWidget(0, 0, 1, 5, Component.empty()) {
    override fun extractWidgetRenderState(
        graphics: GuiGraphicsExtractor,
        mouseX: Int,
        mouseY: Int,
        a: Float
    ) {
        graphics.fill(x, y, x + width, y + height, 0x000000 opacity 64)
    }

    override fun updateWidgetNarration(output: NarrationElementOutput) = Unit
}