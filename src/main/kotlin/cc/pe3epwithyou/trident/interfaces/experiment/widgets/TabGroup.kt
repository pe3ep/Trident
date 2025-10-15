package cc.pe3epwithyou.trident.interfaces.experiment.widgets

import com.noxcrew.sheeplib.CompoundWidget
import com.noxcrew.sheeplib.layout.linear
import com.noxcrew.sheeplib.theme.Themed
import net.minecraft.client.gui.layouts.LinearLayout

class TabGroup(
    dialog: Themed,
    tabs: List<Tab>,
    view: TabView
) : CompoundWidget(0, 0, 0, 0), Themed by dialog {
    override val layout = linear(
        LinearLayout.Orientation.HORIZONTAL
    ) {
        tabs.forEach { tab ->
            +TabButton(this@TabGroup, tab = tab) {
                view.currentTab = tab
            }
        }
    }

    init {
        layout.arrangeElements()
        layout.visitWidgets(this::addChild)
    }
}