package cc.pe3epwithyou.trident.utils

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.render.state.GuiItemRenderState
import net.minecraft.client.renderer.item.TrackingItemStackRenderState
import net.minecraft.core.component.DataComponents
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.joml.Matrix3x2f


data class Model(
    val modelPath: ResourceLocation,
    val width: Int,
    val height: Int,
    val damagePercent: Int = 100
) {
    private val item = ItemStack(Items.ECHO_SHARD)

    init {
        item.set(DataComponents.ITEM_MODEL, modelPath)
        item.set(DataComponents.MAX_STACK_SIZE, 1)
        item.set(DataComponents.MAX_DAMAGE, 100)
        item.set(DataComponents.DAMAGE, 100 - damagePercent)
    }

    /**
     * Blits the item model onto the screen.
     *
     * @param guiGraphics a [GuiGraphics] instance to render with
     * @param x the X coordinate to blit to (left edge)
     * @param y the Y coordinate to blit to (top edge)
     */
    fun render(guiGraphics: GuiGraphics, x: Int, y: Int) {
        val client = Minecraft.getInstance()
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