package cc.pe3epwithyou.trident.utils

import net.minecraft.client.gui.GuiGraphics

object GraphicsExtensions {
    fun GuiGraphics.fillRoundedAll(x: Int, y: Int, width: Int, height: Int, color: Int) {
        this.fill(
            x,
            y + 1,
            x + 1,
            y + height - 1,
            color
        )
        this.fill(
            x + 1,
            y,
            x + width - 1,
            y + height,
            color
        )
        this.fill(
            x + width - 1,
            y + 1,
            x + width,
            y + height - 1,
            color
        )
    }

    fun GuiGraphics.fillRoundedLeft(x: Int, y: Int, width: Int, height: Int, color: Int) {
        this.fill(
            x,
            y + 1,
            x + 1,
            y + height - 1,
            color
        )
        this.fill(
            x + 1,
            y,
            x + width,
            y + height,
            color
        )
    }

    fun GuiGraphics.fillRoundedRight(x: Int, y: Int, width: Int, height: Int, color: Int) {
        this.fill(
            x,
            y,
            x + width - 1,
            y + height,
            color
        )
        this.fill(
            x + width - 1,
            y + 1,
            x + width,
            y + height - 1,
            color
        )
    }
}