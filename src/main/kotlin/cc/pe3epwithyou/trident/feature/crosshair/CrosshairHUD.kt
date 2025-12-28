package cc.pe3epwithyou.trident.feature.crosshair

import cc.pe3epwithyou.trident.config.Config.Companion.handler
import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.utils.Model
import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.Texture
import com.noxcrew.sheeplib.util.opaqueColor
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.Items
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

        renderCrossbowDisplay(graphics)

        repeat(6) {
            val distanceX = handler.instance().crosshairHudDistanceX
            val distanceY = handler.instance().crosshairHudDistanceY
            val (x, y) = getSlotPosition(
                it,
                width,
                height,
                distanceX = distanceX,
                distanceY = distanceY
            )
            texture.render(graphics, x, y)
//            graphics.drawString(Minecraft.getInstance().font, "$it", x, y, 0xffffff.opaqueColor())
        }
    }

    fun renderCrossbowDisplay(
        graphics: GuiGraphics,
        width: Int = graphics.guiWidth(),
        height: Int = graphics.guiHeight(),
        x: Int = 0,
        y: Int = 0
    ) {
        val client = Minecraft.getInstance()
        val player = client.player ?: return
        if (!player.isHolding(Items.CROSSBOW)) return
        val crossbow = when {
            player.mainHandItem.item == Items.CROSSBOW -> player.mainHandItem
            player.offhandItem.item == Items.CROSSBOW -> player.offhandItem
            else -> return
        }

        if (crossbow.get(DataComponents.CHARGED_PROJECTILES)?.isEmpty ?: true) return

        Texture(
            location = Resources.trident("textures/gui/sprites/hud/crossbow_charged.png"),
            width = 6,
            height = 6,
            textureWidth = 8,
            textureHeight = 8,
            pipeline = RenderPipelines.CROSSHAIR
        ).blit(
            graphics,
            x = x + (width - 6) / 2,
            y = y + (height - 6) / 2 - 14,
        )
    }

    fun getSlotPosition(
        index: Int,
        width: Int,
        height: Int,
        originX: Int = 0,
        originY: Int = 0,
        distanceX: Int,
        distanceY: Int
    ): Pair<Int, Int> {
        val i = index.coerceIn(0, 5)

        val centerX = (width - SPRITE_SIZE) / 2
        val centerY = (height - SPRITE_SIZE) / 2 + OFFSET_Y

        val half = SPRITE_SIZE / 2
        val staggerX = SPRITE_SIZE + 2
        val staggerY = SPRITE_SIZE + 3
        return when (i) {
            0 -> {
                val x = originX + centerX - (OFFSET_X + distanceX) + staggerX * 2 + half
                val y = originY + centerY + (OFFSET_Y + distanceY) - half

                Pair(x, y)
            }

            1 -> {
                val x = originX + centerX + (OFFSET_X + distanceX) - staggerX * 2 - half + 1
                val y = originY + centerY + (OFFSET_Y + distanceY) - half

                Pair(x, y)
            }

            2 -> {
                val x = originX + centerX - (OFFSET_X + distanceX) + staggerX + half
                val y = originY + centerY + (OFFSET_Y + distanceY) + staggerY - half

                Pair(x, y)
            }

            3 -> {
                val x = originX + centerX + (OFFSET_X + distanceX) - staggerX - half + 1
                val y = originY + centerY + (OFFSET_Y + distanceY) + staggerY - half

                Pair(x, y)
            }

            4 -> {
                val x = originX + centerX - (OFFSET_X + distanceX) + staggerX * 2 + half
                val y = originY + centerY + (OFFSET_Y + distanceY) + staggerY * 2 - half

                Pair(x, y)
            }

            else -> {
                val x = originX + centerX + (OFFSET_X + distanceX) - staggerX * 2 - half + 1
                val y = originY + centerY + (OFFSET_Y + distanceY) + staggerY * 2 - half

                Pair(x, y)
            }
        }
    }


}