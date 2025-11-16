package cc.pe3epwithyou.trident.interfaces.experiment.widgets

import cc.pe3epwithyou.trident.utils.extensions.GraphicsExtensions.fillRoundedAll
import com.noxcrew.sheeplib.theme.Theme
import com.noxcrew.sheeplib.theme.Themed
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput

class TabShort(
    val theme: Themed,
    val tab: Tab,
    val view: TabView,
    val style: Theme.ButtonStyle = theme.theme.buttonStyles.standard,
) : AbstractWidget(0, 0, 14, 14, tab.title) {
    override fun renderWidget(
        graphics: GuiGraphics, i: Int, j: Int, f: Float
    ) {
        graphics.fillRoundedAll(
            x, y, getWidth(), getHeight(), when {
                isHovered -> style.hoverColor
                tab.isDetached -> style.disabledColor
                else -> style.defaultColor
            }.get(theme.theme)
        )
        when {
            tab.isDetached -> Tab.DETACH_ICON
            else -> tab.icon
        }.blit(
            graphics,
            x + 3,
            y + 3,
        )
    }

    override fun onClick(d: Double, e: Double) {
        if (!tab.disabled) view.changeTab(tab)
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {
        super.defaultButtonNarrationText(narrationElementOutput)
    }

}