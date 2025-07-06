package cc.pe3epwithyou.trident.dialogs

import cc.pe3epwithyou.trident.dialogs.themes.DialogTheme
import cc.pe3epwithyou.trident.dialogs.themes.DialogTitle
import cc.pe3epwithyou.trident.utils.TridentFont
import com.noxcrew.sheeplib.LayoutConstants
import com.noxcrew.sheeplib.dialog.Dialog
import com.noxcrew.sheeplib.dialog.title.DialogTitleWidget
import com.noxcrew.sheeplib.dialog.title.TextTitleWidget
import com.noxcrew.sheeplib.layout.grid
import com.noxcrew.sheeplib.theme.DefaultTheme
import com.noxcrew.sheeplib.theme.Themed
import com.noxcrew.sheeplib.util.opacity
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.layouts.GridLayout
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style

class SuppliesDialog(x: Int, y: Int) : Dialog(x, y), Themed by DialogTheme {
    private fun getWidgetTitle(): DialogTitleWidget {
//        val icon = Component.literal("\uE0E4").withStyle(Style.EMPTY.withFont(TridentFont.getMCCFont("icon")))
        val icon = Component.literal("\uE0E4").withStyle(Style.EMPTY.withFont(TridentFont.getMCCFont("icon")))
        val text = Component.literal(" Supplies".uppercase()).withStyle(Style.EMPTY.withFont(TridentFont.getMCCFont()))
        val titleWidget = DialogTitle(this, icon.append(text), 0x640000 opacity 63)
        return titleWidget
    }
    override val title = getWidgetTitle()
    override fun layout(): GridLayout = grid {
        val mcFont = Minecraft.getInstance().font
        StringWidget(Component.literal("Hello world"), mcFont).at(0,0, 1, 1).width = 100
    }
}