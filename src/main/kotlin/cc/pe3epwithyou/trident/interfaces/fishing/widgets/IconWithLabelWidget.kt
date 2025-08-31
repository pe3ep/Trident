package cc.pe3epwithyou.trident.interfaces.fishing.widgets

import cc.pe3epwithyou.trident.interfaces.shared.widgets.ItemWidget
import cc.pe3epwithyou.trident.utils.Model
import com.noxcrew.sheeplib.CompoundWidget
import com.noxcrew.sheeplib.layout.CanvasLayout
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.network.chat.Component
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.mccFont

class IconWithLabelWidget(
    private val model: Model,
    private val label: Component?,
    private val marginRight: Int = 0
) : CompoundWidget(0, 0, model.width + marginRight, 0) {

    override val layout: CanvasLayout = CanvasLayout(
        model.width,
        model.height + 9,
    ).apply {
        ItemWidget(model).at(top = 0, left = 0)
        label?.let {
            val font = Minecraft.getInstance().font
            var chosen = it.copy().mccFont(offset = 0)
            var widthPx = font.width(chosen.visualOrderText)
            val offsets = listOf(3, 2, 1, 0)
            for (off in offsets) {
                val candidate = it.copy().mccFont(offset = off)
                val w = font.width(candidate.visualOrderText)
                if (w <= model.width) {
                    chosen = candidate
                    widthPx = w
                    break
                }
                chosen = candidate
                widthPx = w
            }
            val left = (model.width - widthPx) / 2
            StringWidget(chosen, font).at(top = model.height, left = left)
        }
    }

    override fun getWidth(): Int = model.width + marginRight
    override fun getHeight(): Int = layout.height

    init {
        layout.arrangeElements()
        layout.visitWidgets(this::addChild)
    }
}


