package cc.pe3epwithyou.trident.utils

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.world.item.ItemStack

class ItemRenderer(
    val item: ItemStack,
    val width: Int,
    val height: Int
) {
    fun render(guiGraphics: GuiGraphicsExtractor, x: Int, y: Int) {
        val client = minecraft()
        val font = client.font
        guiGraphics.item(item, x, y)
        guiGraphics.itemDecorations(font, item, x, y)
    }
}
