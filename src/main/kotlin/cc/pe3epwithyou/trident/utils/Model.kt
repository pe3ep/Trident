package cc.pe3epwithyou.trident.utils

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.core.component.DataComponents
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items


data class Model(
    val modelPath: Identifier,
    val width: Int,
    val height: Int,
    val damage: Float = 1.0f,
) {
    private val item = ItemStack(Items.ECHO_SHARD)
    private var renderer: ItemRenderer

    init {
        item.set(DataComponents.ITEM_MODEL, modelPath)
        item.set(DataComponents.MAX_STACK_SIZE, 1)
        item.set(DataComponents.MAX_DAMAGE, 100)
        item.set(DataComponents.DAMAGE, (100 * (1 - damage)).toInt())

        renderer = ItemRenderer(item, width, height)
    }

    /**
     * Blits the item model onto the screen.
     *
     * @param guiGraphics a [GuiGraphics] instance to render with
     * @param x the X coordinate to blit to (left edge)
     * @param y the Y coordinate to blit to (top edge)
     */
    fun render(guiGraphics: GuiGraphics, x: Int, y: Int) = renderer.render(guiGraphics, x, y)
}