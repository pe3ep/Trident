package cc.pe3epwithyou.trident.interfaces.experiment.widgets

import cc.pe3epwithyou.trident.interfaces.shared.TridentDialog
import cc.pe3epwithyou.trident.interfaces.shared.widgets.LayoutPortal
import com.noxcrew.sheeplib.CompoundWidget
import com.noxcrew.sheeplib.layout.grid
import com.noxcrew.sheeplib.theme.Themed
import net.minecraft.client.gui.layouts.Layout

class TabView(
    private val dialog: TridentDialog,
    val tabs: List<Tab>,
    val currentTab: Tab,
    val tabSetter: (Tab) -> Unit,
) : CompoundWidget(0, 0, 0, 0), Themed by dialog {
    override fun getWidth() = layout.width
    override fun getHeight() = layout.height

    fun changeTab(new: Tab) = tabSetter(new)

    override val layout: Layout = grid {
        TabButtonGroup(dialog, tabs, currentTab, this@TabView).atBottom(0)
        LayoutPortal(currentTab.layout()).atBottom(0)
    }

    init {
        layout.arrangeElements()
        layout.visitWidgets(this::addChild)
    }
}