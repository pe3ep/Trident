package cc.pe3epwithyou.trident.utils

import com.noxcrew.sheeplib.util.opaqueColor
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation

public data class Texture(
    val location: ResourceLocation,
    val height: Int,
    val width: Int,
    val textureWidth: Int = width,
    val textureHeight: Int = height,
    val hoverLocation: ResourceLocation = location,
) {

    /**
     * Blits the texture onto the screen.
     *
     * @param guiGraphics a [GuiGraphics] instance to render with
     * @param x the X coordinate to blit to (left edge)
     * @param y the Y coordinate to blit to (top edge)
     * @param scale a factor to scale the icon up by. Defaults to 1.
     */
    public fun blit(guiGraphics: GuiGraphics, x: Int, y: Int, scale: Int = 1, isHovered: Boolean = false) {
//        guiGraphics.fill(x, y, x + 12, y + 12, 0xFF0000.opaqueColor())
        guiGraphics.blit(
            RenderType::guiTextured,
            if (isHovered) hoverLocation else location,
            x,
            y,
            0f,
            0f,
            width * scale,
            height * scale,
            textureWidth * scale,
            textureWidth * scale,
            textureWidth,
            textureHeight
        )
    }
}