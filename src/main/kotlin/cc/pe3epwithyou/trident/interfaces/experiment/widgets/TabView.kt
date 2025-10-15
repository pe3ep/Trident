package cc.pe3epwithyou.trident.interfaces.experiment.widgets

import cc.pe3epwithyou.trident.interfaces.shared.widgets.LayoutPortal
import com.noxcrew.sheeplib.CompoundWidget
import com.noxcrew.sheeplib.layout.grid
import com.noxcrew.sheeplib.theme.Themed
import net.minecraft.client.gui.layouts.Layout

class TabView(
    dialog: Themed,
    val tabs: List<Tab>,
    var currentTab: Tab = tabs.first(),
) : CompoundWidget(0, 0, 0, 0), Themed by dialog {
    override val layout: Layout = grid {
        TabGroup(dialog, tabs, this@TabView).atBottom(0)
        LayoutPortal(currentTab.layout).atBottom(0)
    }

    init {
        layout.arrangeElements()
        layout.visitWidgets(this::addChild)
    }
}