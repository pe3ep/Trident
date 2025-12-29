package cc.pe3epwithyou.trident.interfaces.experiment

import cc.pe3epwithyou.trident.interfaces.experiment.widgets.Tab
import cc.pe3epwithyou.trident.interfaces.experiment.widgets.TabView
import cc.pe3epwithyou.trident.interfaces.shared.TridentDialog
import cc.pe3epwithyou.trident.interfaces.shared.widgets.LayoutPortal
import cc.pe3epwithyou.trident.interfaces.themes.DialogTitle
import cc.pe3epwithyou.trident.interfaces.themes.TridentThemed
import com.noxcrew.sheeplib.layout.grid
import com.noxcrew.sheeplib.theme.Themed
import com.noxcrew.sheeplib.util.opacity

class DetachedTabDialog(x: Int, y: Int, key: String, val tab: Tab, view: TabView) :
    TridentDialog(x, y, key),
    Themed by TridentThemed {
    override val title = DialogTitle(
        this@DetachedTabDialog,
        tab.title,
        color = 0xFF0000 opacity 128,
        tab = tab,
        tabView = view,
        isCloseable = false
    )

    override fun layout() = grid {
        LayoutPortal(tab.layout()).atBottom(0)
    }
}