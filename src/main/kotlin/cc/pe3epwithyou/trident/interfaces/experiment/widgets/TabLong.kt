package cc.pe3epwithyou.trident.interfaces.experiment.widgets

import cc.pe3epwithyou.trident.utils.Texture
import cc.pe3epwithyou.trident.utils.extensions.GraphicsExtensions.fillRoundedAll
import com.noxcrew.sheeplib.CompoundWidget
import com.noxcrew.sheeplib.layout.linear
import com.noxcrew.sheeplib.theme.Theme
import com.noxcrew.sheeplib.theme.Themed
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component

class TabLong(
    val themed: Themed,
    val tab: Tab,
    val style: Theme.ButtonStyle = themed.theme.buttonStyles.standard,
) : CompoundWidget(0, 0, themed.theme.dimensions.buttonWidth, themed.theme.dimensions.buttonHeight),
    Themed by themed.theme {

    override val layout: LinearLayout = linear(
        LinearLayout.Orientation.HORIZONTAL
    ) {
        val font = Minecraft.getInstance().font
        +IconWidget(themed, tab.icon)
        +StringWidget(tab.title, font)
    }

    override fun renderWidget(graphics: GuiGraphics, i: Int, j: Int, f: Float) {
        graphics.fillRoundedAll(
            x,
            y,
            getWidth(),
            getHeight(),
            when {
                isHovered() -> style.hoverColor
                else -> style.defaultColor
            }.get(themed.theme)
        )
        super.renderWidget(graphics, i, j, f)
    }

    init {
        layout.arrangeElements()
        layout.visitWidgets(this::addChild)
    }
}

private class IconWidget(
    val themed: Themed,
    val icon: Texture,
    size: Int = FONT_HEIGHT + themed.theme.dimensions.paddingInner * 2
) : AbstractWidget(0, 0, size, size, Component.empty()) {
    companion object {
        const val FONT_HEIGHT: Int = 8
    }

    override fun renderWidget(
        graphics: GuiGraphics,
        i: Int,
        j: Int,
        f: Float
    ) {
        icon.blit(
            graphics,
            x + themed.theme.dimensions.paddingInner,
            y,
        )
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput): Unit = Unit

}