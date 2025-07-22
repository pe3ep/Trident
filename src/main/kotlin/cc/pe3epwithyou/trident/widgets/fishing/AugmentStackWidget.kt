package cc.pe3epwithyou.trident.widgets.fishing

import cc.pe3epwithyou.trident.state.fishing.Augment
import cc.pe3epwithyou.trident.utils.Texture
import cc.pe3epwithyou.trident.widgets.IconWidget
import com.noxcrew.sheeplib.CompoundWidget
import com.noxcrew.sheeplib.layout.LinearLayout
import com.noxcrew.sheeplib.theme.Themed
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.resources.ResourceLocation
import kotlin.enums.EnumEntries

class AugmentStackWidget(width: Int, height: Int, theme: Themed, entries: List<Augment>) : CompoundWidget(0, 0, width, height) {

    override val layout: LinearLayout = LinearLayout(
        LinearLayout.Orientation.HORIZONTAL,
        0,
    ) {
        entries.forEach { augment ->
            +IconWidget(
                Texture(
                    augment.texturePath,
                    width,
                    height,
                    augment.textureWidth,
                    augment.textureHeight
                ),
                marginRight = 2
            )
        }
    }

    override fun mouseClicked(d: Double, e: Double, i: Int): Boolean = false

    init {
        layout.arrangeElements()
        layout.visitWidgets(this::addChild)
    }
}