package cc.pe3epwithyou.trident.interfaces.experiment.widgets

import cc.pe3epwithyou.trident.utils.extensions.GraphicsExtensions.fillRoundedAll
import com.noxcrew.sheeplib.CompoundWidget
import com.noxcrew.sheeplib.layout.linear
import com.noxcrew.sheeplib.theme.Themed
import com.noxcrew.sheeplib.util.opacity
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.layouts.LinearLayout

class TabButtonGroup(
    private val dialog: Themed,
    tabs: List<Tab>,
    currentTab: Tab,
    val view: TabView
) : CompoundWidget(0, 0, 0, 0), Themed by dialog {
    override fun getWidth() = layout.width
    override fun getHeight() = layout.height

    override val layout = linear(
        LinearLayout.Orientation.HORIZONTAL
    ) {
        tabs.forEach { tab ->
            +TabButton(
                theme = this@TabButtonGroup,
                tab = tab,
                view = view,
                style = if (tab == currentTab) theme.buttonStyles.positive else theme.buttonStyles.standard
            )
        }
    }

    override fun renderWidget(graphics: GuiGraphics, i: Int, j: Int, f: Float) {
        graphics.fillRoundedAll(
            x,
            y,
            getWidth(),
            getHeight(),
            0x111111 opacity 64
        )
        super.renderWidget(graphics, i, j, f)
    }

    init {
        layout.arrangeElements()
        layout.visitWidgets(this::addChild)
    }
}