package cc.pe3epwithyou.trident.interfaces.experiment

import cc.pe3epwithyou.trident.interfaces.experiment.widgets.Tab
import cc.pe3epwithyou.trident.interfaces.experiment.widgets.TabView
import cc.pe3epwithyou.trident.interfaces.shared.TridentDialog
import cc.pe3epwithyou.trident.interfaces.themes.TridentThemed
import com.noxcrew.sheeplib.layout.grid
import com.noxcrew.sheeplib.theme.Themed
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.network.chat.Component

class TabbedDialog(x: Int, y: Int, key: String) : TridentDialog(x, y, key), Themed by TridentThemed {
    private var tabs: List<Tab> = listOf(
        Tab(
            title = Component.literal("Ice Cream"),
            layout = iceCreamTab()
        ),
        Tab(
            title = Component.literal("Burger"),
            layout = burgerTab()
        )
    )

    override fun layout() = grid {
        TabView(this@TabbedDialog, tabs).atBottom(0)
    }

    private fun iceCreamTab() = grid {
        val font = Minecraft.getInstance().font
        StringWidget(Component.literal("I love ice cream!"), font).atBottom(0)
    }

    private fun burgerTab() = grid {
        val font = Minecraft.getInstance().font
        StringWidget(Component.literal("I love burgers yahee woo!!!!"), font).atBottom(0)
        StringWidget(Component.literal("borger :O"), font).atBottom(0)
    }
}