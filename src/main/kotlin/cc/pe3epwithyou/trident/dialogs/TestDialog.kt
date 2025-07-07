package cc.pe3epwithyou.trident.dialogs

import cc.pe3epwithyou.trident.dialogs.themes.DialogTheme
import cc.pe3epwithyou.trident.dialogs.themes.DialogTitle
import com.noxcrew.sheeplib.dialog.Dialog
import com.noxcrew.sheeplib.dialog.title.DialogTitleWidget
import com.noxcrew.sheeplib.layout.grid
import com.noxcrew.sheeplib.layout.iconButtonRow
import com.noxcrew.sheeplib.theme.Theme
import com.noxcrew.sheeplib.theme.Themed
import com.noxcrew.sheeplib.util.ComponentBuilder
import com.noxcrew.sheeplib.widget.ButtonStack
import com.noxcrew.sheeplib.widget.SliderWidget
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.layouts.Layout
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextColor

public class TestDialog(x: Int, y: Int) : Dialog(x, y), Themed by DialogTheme {

    private var index: Int = 0

    override fun renderWidget(graphics: GuiGraphics, i: Int, j: Int, f: Float) {
        graphics.pose().pushPose()
        graphics.pose().translate(0f, 0f, index.toFloat())
        super.renderWidget(graphics, i, j, f)
        graphics.pose().popPose()
    }

    override fun layout(): Layout = grid {
        SliderWidget(500, 0, 100, this@TestDialog) {
            index = it
        }.atBottom(0)
    }
}