package cc.pe3epwithyou.trident.widgets

import cc.pe3epwithyou.trident.utils.Texture
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component

public class IconWidget(
    private val icon: Texture,
    private val hoverText: Component? = null,
    private val marginRight: Int = 0
) :
    AbstractWidget(0, 0, icon.width + marginRight, icon.height, Component.empty()) {
    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        icon.blit(guiGraphics, x, y, isHovered = isHovered())
        if (isHovered() && hoverText != null) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font, hoverText, x, y)
        }
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput): Unit = Unit
}