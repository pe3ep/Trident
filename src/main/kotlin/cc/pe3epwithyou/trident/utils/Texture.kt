package cc.pe3epwithyou.trident.utils

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation

public data class Texture(
    val location: ResourceLocation,
    val width: Int,
    val height: Int,
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
    fun blit(guiGraphics: GuiGraphics, x: Int, y: Int, scale: Int = 1) {
        guiGraphics.blit(
            RenderPipelines.GUI_TEXTURED,
            location,
            x,
            y,
            0f,
            0f,
            width * scale,
            height * scale,
            textureWidth * scale,
            textureHeight * scale,
            textureWidth,
            textureHeight
        )
    }
}