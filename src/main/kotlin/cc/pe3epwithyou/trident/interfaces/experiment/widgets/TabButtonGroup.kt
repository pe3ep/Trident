package cc.pe3epwithyou.trident.interfaces.experiment.widgets

import cc.pe3epwithyou.trident.utils.extensions.GraphicsExtensions.fillRoundedAll
import com.noxcrew.sheeplib.CompoundWidget
import com.noxcrew.sheeplib.layout.grid
import com.noxcrew.sheeplib.theme.Themed
import com.noxcrew.sheeplib.util.opacity
import net.minecraft.client.gui.GuiGraphics

class TabButtonGroup(
    private val dialog: Themed, tabs: List<Tab>, currentTab: Tab, val view: TabView
) : CompoundWidget(0, 0, 0, 0), Themed by dialog {
    override fun getWidth() = layout.width
    override fun getHeight() = layout.height

    override val layout = grid {
        var col = 0
        tabs.forEach { tab ->
            if (tab == currentTab) {
                TabLong(
                    themed = this@TabButtonGroup, tab = tab, style = theme.buttonStyles.positive, view = view
                ).at(0, col)
            } else {
                TabShort(
                    theme = this@TabButtonGroup, tab = tab, view = view, style = theme.buttonStyles.standard
                ).at(0, col)
            }
            col++
        }
    }

    override fun renderWidget(graphics: GuiGraphics, i: Int, j: Int, f: Float) {
        graphics.fillRoundedAll(
            x, y, getWidth(), getHeight(), 0x111111 opacity 64
        )
        super.renderWidget(graphics, i, j, f)
    }

    init {
        layout.arrangeElements()
        layout.visitWidgets(this::addChild)
    }
}