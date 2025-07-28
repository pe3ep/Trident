package cc.pe3epwithyou.trident.widgets.fishing

import cc.pe3epwithyou.trident.state.fishing.OverclockTexture
import cc.pe3epwithyou.trident.utils.Texture
import cc.pe3epwithyou.trident.widgets.IconWidget
import com.noxcrew.sheeplib.CompoundWidget
import com.noxcrew.sheeplib.layout.LinearLayout
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.network.chat.Component

class UnstableOverclockWidget(width: Int, height: Int, overclockTexture: OverclockTexture, private val marginRight: Int = 0, timeComponent: Component) : CompoundWidget(0, 0, width, height) {
    override val layout = LinearLayout(
        LinearLayout.Orientation.HORIZONTAL,
        0,
    ) {
        val mcfont = Minecraft.getInstance().font
        +IconWidget(
            Texture(
                overclockTexture.texturePath,
                height,
                height,
                overclockTexture.textureWidth,
                overclockTexture.textureHeight
            ),
            marginRight = 2
        )
        +StringWidget(timeComponent, mcfont).apply {
            alignLeft()
        }
    }

    override fun getHeight(): Int = layout.height

    override fun getWidth(): Int = 46 + marginRight

    init {
        layout.arrangeElements()
        layout.visitWidgets(this::addChild)
    }
}