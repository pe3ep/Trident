package cc.pe3epwithyou.trident.interfaces.fishing.widgets

import cc.pe3epwithyou.trident.state.AugmentContainer
import com.noxcrew.sheeplib.CompoundWidget
import com.noxcrew.sheeplib.layout.LinearLayout
import net.minecraft.client.gui.layouts.LinearLayout

class AugmentStackWidget(width: Int, height: Int, entries: List<AugmentContainer>) :
    CompoundWidget(0, 0, width, height) {

    override val layout: LinearLayout = LinearLayout(
        LinearLayout.Orientation.HORIZONTAL,
        0,
    ) {
        entries.forEachIndexed { i, augmentContainer ->
            +AugmentWidget(
                width,
                height,
                augmentContainer,
                if (i == entries.lastIndex) 0 else 2
            )
        }
    }

    override fun mouseClicked(d: Double, e: Double, i: Int): Boolean = false
    override fun getWidth(): Int = layout.width
    override fun getHeight(): Int = layout.height

    init {
        layout.arrangeElements()
        layout.visitWidgets(this::addChild)
    }
}