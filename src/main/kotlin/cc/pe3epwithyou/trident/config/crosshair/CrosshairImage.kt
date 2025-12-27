package cc.pe3epwithyou.trident.config.crosshair

import cc.pe3epwithyou.trident.feature.crosshair.CrosshairHUD.SPRITE_SIZE
import cc.pe3epwithyou.trident.feature.crosshair.CrosshairHUD.getSlotPosition
import cc.pe3epwithyou.trident.feature.crosshair.UsableItem
import cc.pe3epwithyou.trident.utils.Model
import cc.pe3epwithyou.trident.utils.Resources
import com.noxcrew.sheeplib.util.opaqueColor
import dev.isxander.yacl3.gui.image.ImageRenderer
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderPipelines

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

//        val SPRITE = Resources.minecraft("spectral_arrow")
        val sprites: List<UsableItem> = listOf(
            UsableItem.ARROW,
            UsableItem.SPEED_SPARK,
            UsableItem.HARMING_LONG_TIMED_BALL,
            UsableItem.COBWEB,
            UsableItem.BLINDNESS_SHORT_TIMED_ORB,
            UsableItem.GLOWING_BALL,
        )

        graphics.blitSprite(
            RenderPipelines.CROSSHAIR,
            Resources.minecraft("hud/crosshair"),
            x + (width - 15) / 2,
            y + (height - 15) / 2,
            15,
            15
        )
        sprites.forEachIndexed { index, item ->
            val (x, y) = getSlotPosition(index, width, height, x, y, offsetX, offsetY)
            Model(
                item.sprite,
                SPRITE_SIZE,
                SPRITE_SIZE,
            ).render(graphics, x, y)
        }
        return height
    }

    override fun close() = Unit
}