package cc.pe3epwithyou.trident.interfaces.experiment

import cc.pe3epwithyou.trident.interfaces.experiment.widgets.Tab
import cc.pe3epwithyou.trident.interfaces.experiment.widgets.TabView
import cc.pe3epwithyou.trident.interfaces.shared.TridentDialog
import cc.pe3epwithyou.trident.interfaces.themes.DialogTitle
import cc.pe3epwithyou.trident.interfaces.themes.TridentThemed
import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.Texture
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.mccFont
import com.noxcrew.sheeplib.dialog.title.DialogTitleWidget
import com.noxcrew.sheeplib.layout.grid
import com.noxcrew.sheeplib.theme.Themed
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.network.chat.Component

class TabbedDialog(x: Int, y: Int, key: String) : TridentDialog(x, y, key),
    Themed by TridentThemed {
    private companion object {
        private val ICE_CREAM_ICON: Texture
            get() = Texture(Resources.mcc("textures/_fonts/icon/quest_log.png"), 8, 8)
        private val BURGER_ICON: Texture
            get() = Texture(Resources.mcc("textures/_fonts/icon/emojis/burger.png"), 8, 8)
        private val FRIES_ICON: Texture
            get() = Texture(Resources.mcc("textures/_fonts/icon/emojis/chicken.png"), 8, 8)
    }

    private var tabs: List<Tab> = listOf(
        Tab(
            title = Component.literal("Ice Cream".uppercase()).mccFont(),
            icon = ICE_CREAM_ICON,
            layout = ::iceCreamTab,
            id = "ice_cream",
        ), Tab(
            title = Component.literal("Burger".uppercase()).mccFont(),
            icon = BURGER_ICON,
            layout = ::burgerTab,
            id = "burger",
        ), Tab(
            title = Component.literal("Fries".uppercase()).mccFont(),
            icon = FRIES_ICON,
            layout = ::friesTab,
            id = "fries",
        )
    )

    override val title: DialogTitleWidget =
        DialogTitle(this@TabbedDialog, Component.literal("Tabbed"))

    private var currentTab: Tab = tabs.first()
    fun setTab(t: Tab) {
        currentTab = t
        refresh()
    }

    // Main layout
    override fun layout() = grid {
        TabView(this@TabbedDialog, tabs, currentTab, ::setTab).atBottom(0)
    }

    private fun iceCreamTab() = grid {
        val font = Minecraft.getInstance().font
        StringWidget(Component.literal("I love ice cream!"), font).atBottom(0)
    }

    private fun burgerTab() = grid {
        val font = Minecraft.getInstance().font
        StringWidget(Component.literal("I love burgers!!"), font).atBottom(0)
        StringWidget(Component.literal("borger :O"), font).atBottom(0)
    }

    private fun friesTab() = grid {
        val font = Minecraft.getInstance().font
        StringWidget(Component.literal("I LOVEE fries"), font).atBottom(0)
        StringWidget(Component.literal("fries never cries"), font).atBottom(0)
    }
}