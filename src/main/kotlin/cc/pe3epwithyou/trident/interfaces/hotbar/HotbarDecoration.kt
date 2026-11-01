package cc.pe3epwithyou.trident.interfaces.hotbar

import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphicsExtractor

fun interface HotbarDecoration {
    /**
     * Renders a hotbar decoration visual element. Used in [HotbarDecorationManager]
     *
     * @return Width of the decoration
     */
    fun render(graphics: GuiGraphicsExtractor, deltaTracker: DeltaTracker, x0: Int, y0: Int): Int
}