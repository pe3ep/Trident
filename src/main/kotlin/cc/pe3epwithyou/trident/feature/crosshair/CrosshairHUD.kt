package cc.pe3epwithyou.trident.feature.crosshair

import cc.pe3epwithyou.trident.config.crosshair.CrosshairImage
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.utils.Model
import cc.pe3epwithyou.trident.utils.Resources
import com.noxcrew.sheeplib.util.opaqueColor
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.item.component.DyedItemColor

object CrosshairHUD {
    const val OFFSET_X = 56
    const val OFFSET_Y = -4
    const val SPRITE_SIZE = 9

    fun render(graphics: GuiGraphics) {
        if (!MCCIState.isOnIsland()) return
        val SPRITE = Resources.minecraft("spectral_arrow")
        val dyedItemColor = DyedItemColor(0xff0000.opaqueColor())

        val client = Minecraft.getInstance()
        val width = client.window.guiScaledWidth
        val height = client.window.guiScaledHeight

        val texture = Model(
            SPRITE,
            SPRITE_SIZE,
            SPRITE_SIZE,
            dyedColor = dyedItemColor,
        )
        repeat(6) {
            val (x, y) = getSlotPosition(it, width, height)
            texture.render(graphics, x, y)
            graphics.drawString(Minecraft.getInstance().font, "$it", x, y, 0xffffff.opaqueColor())
        }
    }

    fun getSlotPosition(index: Int, width: Int, height: Int, originX: Int = 0, originY: Int = 0): Pair<Int, Int> {
        val i = index.coerceIn(0, 5)

        val centerX = (width - SPRITE_SIZE) / 2
        val centerY = (height - SPRITE_SIZE) / 2 + OFFSET_Y

        val customOffsetX = CrosshairImage.offsetX // max ±24
        val customOffsetY = CrosshairImage.offsetY

        val half = SPRITE_SIZE / 2
        val staggerX = SPRITE_SIZE + 2
        val staggerY = SPRITE_SIZE + 3
        return when (i) {
            0 -> {
                val x = originX + centerX - (OFFSET_X + customOffsetX) + staggerX * 2 + half
                val y = originY + centerY + (OFFSET_Y + customOffsetY) - half

                Pair(x, y)
            }

            1 -> {
                val x = originX + centerX + (OFFSET_X + customOffsetX) - staggerX * 2 - half + 1
                val y = originY + centerY + (OFFSET_Y + customOffsetY) - half

                Pair(x, y)
            }

            2 -> {
                val x = originX + centerX - (OFFSET_X + customOffsetX) + staggerX + half
                val y = originY + centerY + (OFFSET_Y + customOffsetY) + staggerY - half

                Pair(x, y)
            }

            3 -> {
                val x = originX + centerX + (OFFSET_X + customOffsetX) - staggerX - half + 1
                val y = originY + centerY + (OFFSET_Y + customOffsetY) + staggerY - half

                Pair(x, y)
            }

            4 -> {
                val x = originX + centerX - (OFFSET_X + customOffsetX) + staggerX * 2 + half
                val y = originY + centerY + (OFFSET_Y + customOffsetY) + staggerY * 2 - half

                Pair(x, y)
            }

            else -> {
                val x = originX + centerX + (OFFSET_X + customOffsetX) - staggerX * 2 - half + 1
                val y = originY + centerY + (OFFSET_Y + customOffsetY) + staggerY * 2 - half

                Pair(x, y)
            }
        }
    }


}