package cc.pe3epwithyou.trident.feature.crosshair

import cc.pe3epwithyou.trident.state.MCCIState
import cc.pe3epwithyou.trident.utils.Model
import cc.pe3epwithyou.trident.utils.Resources
import com.noxcrew.sheeplib.util.opaqueColor
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.item.component.DyedItemColor

object CrosshairHUD {
    const val DISTANCE_X = 28
    const val OFFSET_Y = 8
    const val SPRITE_SIZE = 8
    val SPRITE = Resources.mcc("island_items/battle_box/long_timed_ball")

    fun render(graphics: GuiGraphics) {
        if (!MCCIState.isOnIsland()) return
        val width = graphics.guiWidth()
        val height = graphics.guiHeight()

        val centerX = (width - SPRITE_SIZE) / 2
        val centerY = (height - SPRITE_SIZE) / 2 + OFFSET_Y

        val dyedItemColor = DyedItemColor(0xff0000.opaqueColor())

        val texture = Model(
            SPRITE,
            SPRITE_SIZE,
            SPRITE_SIZE,
            dyedColor = dyedItemColor,
        )
//        texture.render(graphics, centerX - DISTANCE_X, centerY)
//        texture.render(graphics, -1 + centerX + DISTANCE_X, centerY)
//
//        texture.render(graphics, centerX - DISTANCE_X + SPRITE_SIZE / 2 + 1, centerY + SPRITE_SIZE + 4)
//        texture.render(graphics, -1 + centerX + DISTANCE_X - SPRITE_SIZE / 2 - 1, centerY + SPRITE_SIZE + 4)
    }


}