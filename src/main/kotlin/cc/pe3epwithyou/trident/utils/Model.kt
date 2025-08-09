package cc.pe3epwithyou.trident.utils

import com.mojang.blaze3d.platform.Lighting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.component.DataComponents
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

public class Model(
    modelPath: ResourceLocation,
    val width: Int,
    val height: Int,

) {
    private val item = ItemStack(Items.ECHO_SHARD)
    init {
        item.set(DataComponents.ITEM_MODEL, modelPath)
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
        guiGraphics.pose().pushPose()

        guiGraphics.pose().translate(x + (width.toFloat() / 2), y + (width.toFloat() / 2), 150F)
        guiGraphics.pose().scale(width.toFloat(), -width.toFloat(), width.toFloat())
        Lighting.setupForFlatItems()
        client.itemRenderer.renderStatic(
            item,
            ItemDisplayContext.GUI,
            15728880,
            OverlayTexture.NO_OVERLAY,
            guiGraphics.pose(),
            client.renderBuffers().bufferSource(),
            client.level,
            0
        )

        guiGraphics.pose().popPose()
    }
}