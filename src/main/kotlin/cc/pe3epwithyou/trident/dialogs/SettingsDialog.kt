package cc.pe3epwithyou.trident.dialogs

import cc.pe3epwithyou.trident.dialogs.themes.DialogTheme
import cc.pe3epwithyou.trident.dialogs.themes.DialogTitle
import cc.pe3epwithyou.trident.widgets.OptionWidget
import cc.pe3epwithyou.trident.utils.TridentFont
import com.noxcrew.sheeplib.LayoutConstants
import com.noxcrew.sheeplib.dialog.Dialog
import com.noxcrew.sheeplib.dialog.title.DialogTitleWidget
import com.noxcrew.sheeplib.layout.grid
import com.noxcrew.sheeplib.theme.Themed
import com.noxcrew.sheeplib.util.opacity
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.layouts.GridLayout
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style

class SettingsDialog(x: Int, y: Int) : Dialog(x, y), Themed by DialogTheme {
    private fun getTitle(): Component {
        val icon = Component.literal("\uE000").withStyle(Style.EMPTY.withFont(TridentFont.getTridentFont()).withShadowColor(0x0 opacity 0))
        val text = Component.literal(" Trident Settings".uppercase()).withStyle(Style.EMPTY.withFont(TridentFont.getMCCFont()))
        return icon.append(text)
    }
    override val title: DialogTitleWidget = DialogTitle(this, getTitle(), 0x4572e3 opacity 63)
    override fun getWidth() = 200 + theme.dimensions.paddingOuter * 2 + theme.dimensions.paddingInner
    override fun layout(): GridLayout = grid {
        val font = Minecraft.getInstance().font
        StringWidget(Component.literal("TestString"), font).at(0, 0, 1, 2, LayoutConstants.LEFT)
        OptionWidget(Component.literal("Hello!")).at(1, 0)
    }
}