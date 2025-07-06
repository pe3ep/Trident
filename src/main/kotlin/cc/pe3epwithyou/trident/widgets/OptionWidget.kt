package cc.pe3epwithyou.trident.widgets

import cc.pe3epwithyou.trident.dialogs.themes.DialogTheme
import com.noxcrew.sheeplib.CompoundWidget
import com.noxcrew.sheeplib.layout.CanvasLayout
import com.noxcrew.sheeplib.theme.Themed
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.network.chat.Component

class OptionWidget(
    private val component: Component
): CompoundWidget(0, 0, 100, 60), Themed by DialogTheme {
    override fun getWidth(): Int = layout.width
    override fun setWidth(i: Int) {
        layout.width = i
    }

    override val layout: CanvasLayout = CanvasLayout(
        100,
        100
    ).apply {
        val font = Minecraft.getInstance().font
        StringWidget(component, font).at(left = theme.dimensions.paddingOuter, top = theme.dimensions.paddingOuter)
    }

    init {
        layout.arrangeElements()
        layout.visitWidgets(this::addChild)
    }

    override fun renderWidget(graphics: GuiGraphics, i: Int, j: Int, f: Float) {
        graphics.fill(x, y, x + getWidth(), y + height, theme.colors.dialogBackgroundAlt)
        graphics.renderOutline(x, y, getWidth(), height, theme.colors.border)
        super.renderWidget(graphics, i, j, f)
    }
}