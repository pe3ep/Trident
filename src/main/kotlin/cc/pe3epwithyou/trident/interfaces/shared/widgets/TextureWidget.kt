package cc.pe3epwithyou.trident.interfaces.shared.widgets

import cc.pe3epwithyou.trident.utils.Texture
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component

class TextureWidget(
    val texture: Texture
) : AbstractWidget(0, 0, texture.width, texture.height, Component.empty()) {
    override fun renderWidget(
        guiGraphics: GuiGraphics,
        i: Int,
        j: Int,
        f: Float
    ) {
        texture.blit(guiGraphics, x, y)
    }

    override fun onClick(mouseButtonEvent: MouseButtonEvent, bl: Boolean) = Unit

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) = Unit
}