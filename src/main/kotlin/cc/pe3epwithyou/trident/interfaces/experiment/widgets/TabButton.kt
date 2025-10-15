package cc.pe3epwithyou.trident.interfaces.experiment.widgets

import cc.pe3epwithyou.trident.utils.extensions.GraphicsExtensions.fillRoundedAll
import com.noxcrew.sheeplib.theme.Theme
import com.noxcrew.sheeplib.theme.Themed
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput

class TabButton(
    theme: Themed,
    width: Int = theme.theme.dimensions.buttonWidth,
    height: Int = theme.theme.dimensions.buttonHeight,
    var tab: Tab,
    val style: Theme.ButtonStyle = theme.theme.buttonStyles.standard,
    val clickHandler: () -> Unit
) : AbstractWidget(0, 0, width, height, tab.title), Themed by theme {
    override fun renderWidget(
        graphics: GuiGraphics, i: Int, j: Int, f: Float
    ) {
        val minecraft = Minecraft.getInstance()
        graphics.fillRoundedAll(
            x, y, getWidth(), getHeight(), when {
                isHovered() -> style.hoverColor
                tab.disabled -> style.disabledColor
                else -> style.defaultColor
            }.get(theme)
        )
        val textColor = if (tab.disabled) theme.colors.textSecondary else theme.colors.textPrimary
        graphics.drawString(minecraft.font, tab.title, x + theme.dimensions.paddingInner, y, textColor)
    }

    override fun onClick(d: Double, e: Double) {
        if (!tab.disabled) clickHandler()
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput)
    }
}