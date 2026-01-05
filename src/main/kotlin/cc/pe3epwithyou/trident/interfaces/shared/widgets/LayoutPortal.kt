package cc.pe3epwithyou.trident.interfaces.shared.widgets

import com.noxcrew.sheeplib.CompoundWidget
import net.minecraft.client.gui.layouts.Layout
import net.minecraft.client.gui.navigation.ScreenRectangle

class LayoutPortal(override val layout: Layout) : CompoundWidget(0, 0, 0, 0) {
    override fun getWidth(): Int = layout.width
    override fun getHeight(): Int = layout.height

    override fun getRectangle(): ScreenRectangle =
        ScreenRectangle(layout.x, layout.y, layout.width, layout.height)

    init {
        layout.arrangeElements()
        layout.visitWidgets(this::addChild)
    }
}