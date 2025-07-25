package cc.pe3epwithyou.trident.dialogs

import cc.pe3epwithyou.trident.dialogs.themes.DialogTitle
import cc.pe3epwithyou.trident.dialogs.themes.TridentThemed
import com.noxcrew.sheeplib.layout.grid
import com.noxcrew.sheeplib.theme.Themed
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.layouts.GridLayout
import net.minecraft.network.chat.Component

class DebugDialog(x: Int, y: Int, key: String) : TridentDialog(x, y, key), Themed by TridentThemed {
    override val title = DialogTitle(this, Component.literal("Test dialog"))

    override fun layout(): GridLayout = grid {
        val mcFont = Minecraft.getInstance().font

        StringWidget(Component.literal("Test! Very sick! Hello world!"), mcFont)
            .at(0,0)
    }
}