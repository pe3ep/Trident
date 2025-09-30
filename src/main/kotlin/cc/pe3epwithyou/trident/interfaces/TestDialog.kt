package cc.pe3epwithyou.trident.interfaces

import cc.pe3epwithyou.trident.interfaces.fishing.SuppliesDialog
import cc.pe3epwithyou.trident.interfaces.shared.TridentDialog
import cc.pe3epwithyou.trident.interfaces.shared.widgets.LayoutPortal
import cc.pe3epwithyou.trident.interfaces.themes.TridentThemed
import com.noxcrew.sheeplib.layout.grid
import com.noxcrew.sheeplib.theme.Themed
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.layouts.Layout
import net.minecraft.network.chat.Component

class TestDialog(x: Int, y: Int, key: String) : TridentDialog(x, y, key), Themed by TridentThemed {
    override fun layout(): Layout = grid {
        val font = Minecraft.getInstance().font
        StringWidget(Component.literal("test!!!!!"), font).atBottom(0)
        LayoutPortal(SuppliesDialog(0, 0, "supplies").publicLayout()).atBottom(0)
        StringWidget(Component.literal("cool"), font).atBottom(0)
    }
}