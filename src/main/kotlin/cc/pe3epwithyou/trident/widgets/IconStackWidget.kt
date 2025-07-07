package cc.pe3epwithyou.trident.widgets

import cc.pe3epwithyou.trident.utils.Texture
import com.noxcrew.sheeplib.CompoundWidget
import com.noxcrew.sheeplib.layout.LinearLayout
import com.noxcrew.sheeplib.theme.Themed
import com.noxcrew.sheeplib.util.Icon
import com.noxcrew.sheeplib.util.opaqueColor
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.resources.ResourceLocation

class IconStackWidget(width: Int, height: Int, theme: Themed, entries: List<ResourceLocation>) : CompoundWidget(0, 0, width, height) {

    override val layout: LinearLayout = LinearLayout(
        LinearLayout.Orientation.HORIZONTAL,
        0,
    ) {
        entries.forEach { path ->
            +IconWidget(
                Texture(
                    path,
                    height = height,
                    width = width
                ),
                marginRight = 2
            )
        }
    }

    override fun renderWidget(graphics: GuiGraphics, i: Int, j: Int, f: Float) {
//        graphics.fill(x, y, x + (width + 2) * 7, y + height, 0x0000FF.opaqueColor())
        super.renderWidget(graphics, i, j, f)
    }

    init {
        layout.arrangeElements()
        layout.visitWidgets(this::addChild)
    }
}