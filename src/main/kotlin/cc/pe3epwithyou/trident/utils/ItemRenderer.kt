package cc.pe3epwithyou.trident.utils

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.render.state.GuiItemRenderState
import net.minecraft.client.renderer.item.TrackingItemStackRenderState
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import org.joml.Matrix3x2f

class ItemRenderer(
    val item: ItemStack,
    val width: Int,
    val height: Int
) {
    fun render(guiGraphics: GuiGraphics, x: Int, y: Int) {
        val client = minecraft()
        val font = client.font
        guiGraphics.pose().pushMatrix()
        val trackingItemStackRenderState = TrackingItemStackRenderState()
        client.itemModelResolver.updateForTopItem(
            trackingItemStackRenderState,
            item,
            ItemDisplayContext.GUI,
            client.level,
            null,
            0
        )

        val itemMatrix = guiGraphics.pose()
        val scaleFactorX = width / 16F
        val scaleFactorY = height / 16F
        itemMatrix.scaleAround(scaleFactorX, scaleFactorY, x.toFloat(), y.toFloat(), itemMatrix)
        val matrix = Matrix3x2f(guiGraphics.pose())

        guiGraphics.guiRenderState.submitItem(
            GuiItemRenderState(
                item.item.toString(),
                matrix,
                trackingItemStackRenderState,
                x,
                y,
                ScreenRectangle(x, y, width, height)
            )
        )
        guiGraphics.renderItemDecorations(
            font,
            item,
            x,
            y
        )
        guiGraphics.pose().popMatrix()
    }
}