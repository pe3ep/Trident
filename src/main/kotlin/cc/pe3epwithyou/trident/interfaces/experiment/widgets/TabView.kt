package cc.pe3epwithyou.trident.interfaces.experiment.widgets

import cc.pe3epwithyou.trident.interfaces.DialogCollection
import cc.pe3epwithyou.trident.interfaces.experiment.DetachedTabDialog
import cc.pe3epwithyou.trident.interfaces.shared.TridentDialog
import cc.pe3epwithyou.trident.interfaces.shared.widgets.LayoutPortal
import com.noxcrew.sheeplib.CompoundWidget
import com.noxcrew.sheeplib.layout.grid
import com.noxcrew.sheeplib.theme.Themed
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.layouts.Layout
import net.minecraft.network.chat.Component

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
        LayoutPortal(
            if (currentTab.isDetached) detachedLayout() else currentTab.layout()
        ).atBottom(0)
    }

    init {
        layout.arrangeElements()
        layout.visitWidgets(this::addChild)
    }

    private fun detachedLayout() = grid {
        val font = Minecraft.getInstance().font
        StringWidget(Component.literal("This tab is detached"), font).atBottom(0)
    }

    fun detachTab(tab: Tab) {
        if (!tabs.contains(tab)) return
        tabs.filter { t -> t.title == tab.title }[0].isDetached = true
        val key = "detached:${tab.id}"
        DialogCollection.open(key, DetachedTabDialog(0,0, key, tab, this@TabView))
        dialog.refresh()
    }

    fun attachTab(tab: Tab) {
        if (!tabs.contains(tab)) return
        tabs.filter { t -> t.title == tab.title }[0].isDetached = false
        DialogCollection.close("detached:${tab.id}")
        dialog.refresh()
    }
}