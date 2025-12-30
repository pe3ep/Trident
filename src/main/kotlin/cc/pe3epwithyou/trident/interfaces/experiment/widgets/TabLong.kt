package cc.pe3epwithyou.trident.interfaces.experiment.widgets

import cc.pe3epwithyou.trident.interfaces.themes.TabbedDialogTheme
import cc.pe3epwithyou.trident.interfaces.themes.TridentThemed
import cc.pe3epwithyou.trident.utils.extensions.GraphicsExtensions.fillRoundedAll
import com.noxcrew.sheeplib.CompoundWidget
import com.noxcrew.sheeplib.layout.linear
import com.noxcrew.sheeplib.theme.Theme
import com.noxcrew.sheeplib.theme.Themed
import com.noxcrew.sheeplib.util.opacity
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.network.chat.MutableComponent

class TabLong(
    val themed: Themed,
    val tab: Tab,
    val view: TabView,
    val style: Theme.ButtonStyle = themed.theme.buttonStyles.standard,
) : CompoundWidget(0, 0, themed.theme.dimensions.buttonWidth, themed.theme.dimensions.buttonHeight),
    Themed by TridentThemed {

    override val layout: LinearLayout = linear(
        LinearLayout.Orientation.HORIZONTAL
    ) {
        val font = Minecraft.getInstance().font

        val title: MutableComponent = tab.title as MutableComponent

        if (tab.isDetached) {
            title.withColor(0xFFFFFF opacity 128)
        }

        +DetachIconWidget(themed, tab, view, marginX = 3 + 1)
        +StringWidget(title, font)
    }

    override fun renderWidget(graphics: GuiGraphics, i: Int, j: Int, f: Float) {
        graphics.fillRoundedAll(
            x, y, getWidth(), getHeight(), when {
                isHovered() -> if (tab.isDetached) style.disabledColor else style.hoverColor
                tab.isDetached -> style.disabledColor
                else -> style.defaultColor
            }.get(TabbedDialogTheme.theme)
        )
        super.renderWidget(graphics, i, j, f)
    }

    init {
        layout.arrangeElements()
        layout.visitWidgets(this::addChild)
    }
}

