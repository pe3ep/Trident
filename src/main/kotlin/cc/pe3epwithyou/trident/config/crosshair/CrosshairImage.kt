package cc.pe3epwithyou.trident.config.crosshair

import cc.pe3epwithyou.trident.feature.crosshair.CrosshairHUD.SPRITE_SIZE
import cc.pe3epwithyou.trident.feature.crosshair.CrosshairHUD.getSlotPosition
import cc.pe3epwithyou.trident.utils.Model
import cc.pe3epwithyou.trident.utils.Resources
import com.noxcrew.sheeplib.util.opaqueColor
import dev.isxander.yacl3.gui.image.ImageRenderer
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.world.item.component.DyedItemColor

class CrosshairImage : ImageRenderer {
    companion object {
        var offsetX = 0
        var offsetY = 0
    }

    override fun render(
        graphics: GuiGraphics,
        x: Int,
        y: Int,
        width: Int,
        delta: Float
    ): Int {
        val height = 128

        graphics.fill(x, y, x + width, y + height, 0x000000.opaqueColor())

        val SPRITE = Resources.minecraft("spectral_arrow")
        val dyedItemColor = DyedItemColor(0xff0000.opaqueColor())
        val texture = Model(
            SPRITE,
            SPRITE_SIZE,
            SPRITE_SIZE,
            dyedColor = dyedItemColor,
        )

        graphics.blitSprite(
            RenderPipelines.CROSSHAIR,
            Resources.minecraft("hud/crosshair"),
            x + (width - 15) / 2,
            y + (height - 15) / 2,
            15,
            15
        )
        repeat(6) {
            val (x, y) = getSlotPosition(it, width, height, x, y)
            texture.render(graphics, x, y)
//            graphics.drawString(Minecraft.getInstance().font, "$it", x, y, 0xffffff.opaqueColor())
        }

        return height
    }

    override fun close() = Unit
}