package cc.pe3epwithyou.trident.interfaces.shared.widgets

import cc.pe3epwithyou.trident.utils.Model
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component

class ModelWidget(
    private val model: Model,
    marginRight: Int = 0
) :
    AbstractWidget(0, 0, model.width + marginRight, model.height, Component.empty()) {

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        model.render(guiGraphics, x, y)
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput): Unit = Unit
}