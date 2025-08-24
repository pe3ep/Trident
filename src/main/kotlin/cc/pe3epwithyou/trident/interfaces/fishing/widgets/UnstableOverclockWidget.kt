package cc.pe3epwithyou.trident.interfaces.fishing.widgets

import cc.pe3epwithyou.trident.interfaces.shared.widgets.ItemWidget
import cc.pe3epwithyou.trident.state.fishing.OverclockTexture
import cc.pe3epwithyou.trident.utils.Model
import com.noxcrew.sheeplib.CompoundWidget
import com.noxcrew.sheeplib.layout.LinearLayout
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.network.chat.Component

class UnstableOverclockWidget(
    width: Int,
    height: Int,
    overclockTexture: OverclockTexture,
    private val marginRight: Int = 0,
    timeComponent: Component
) : CompoundWidget(0, 0, width, height) {
    override val layout = LinearLayout(
        LinearLayout.Orientation.HORIZONTAL,
        0,
    ) {
        val font = Minecraft.getInstance().font
        +ItemWidget(
            Model(
                overclockTexture.texturePath,
                height,
                height,
            ),
            marginRight = 2
        )
        +StringWidget(timeComponent, font).alignLeft()
    }

    override fun getHeight(): Int = layout.height

    override fun getWidth(): Int = 46 + marginRight

    init {
        layout.arrangeElements()
        layout.visitWidgets(this::addChild)
    }
}