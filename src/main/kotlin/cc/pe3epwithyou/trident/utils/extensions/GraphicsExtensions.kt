package cc.pe3epwithyou.trident.utils.extensions

import net.minecraft.client.gui.GuiGraphicsExtractor

object GraphicsExtensions {
    fun GuiGraphicsExtractor.fillRoundedAll(x: Int, y: Int, width: Int, height: Int, color: Int) {
        if (width == 0 || height == 0) return
        if (width == 1) {
            this.fill(x, y + 1, x + 1, y + height - 1, color)
            return
        }
        if (width == 2) {
            this.fill(x, y + 1, x + 2, y + height - 1, color)
            return
        }

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

    fun GuiGraphicsExtractor.fillRoundedLeft(x: Int, y: Int, width: Int, height: Int, color: Int) {
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

    fun GuiGraphicsExtractor.fillRoundedRight(x: Int, y: Int, width: Int, height: Int, color: Int) {
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

    fun GuiGraphicsExtractor.fillRoundedBottom(x: Int, y: Int, width: Int, height: Int, color: Int) {
        if (width == 0 || height == 0) return
        if (height == 1) {
            this.fill(
                x + 1,
                y + height - 1,
                x + width - 1,
                y + height,
                color
            )
            return
        }
        if (width == 1) {
            this.fill(
                x,
                y,
                x + 1,
                y + height - 1,
                color
            )
            return
        }

        this.fill(
            x,
            y,
            x + width,
            y + height - 1,
            color
        )
        this.fill(
            x + 1,
            y + height - 1,
            x + width - 1,
            y + height,
            color
        )
    }

    fun GuiGraphicsExtractor.renderWithInverseScissor(
        x0: Int,
        y0: Int,
        x1: Int,
        y1: Int,
        block: () -> Unit,
    ) {
        val width = guiWidth()
        val height = guiHeight()

        // Normalize and clamp the inner rectangle.
        val left = minOf(x0, x1).coerceIn(0, width)
        val right = maxOf(x0, x1).coerceIn(0, width)
        val top = minOf(y0, y1).coerceIn(0, height)
        val bottom = maxOf(y0, y1).coerceIn(0, height)

        fun renderClipped(
            sx0: Int,
            sy0: Int,
            sx1: Int,
            sy1: Int,
        ) {
            if (sx0 >= sx1 || sy0 >= sy1) return

            enableScissor(sx0, sy0, sx1, sy1)
            try {
                block()
            } finally {
                disableScissor()
            }
        }

        // Top
        renderClipped(0, 0, width, top)

        // Bottom
        renderClipped(0, bottom, width, height)

        // Left
        renderClipped(0, top, left, bottom)

        // Right
        renderClipped(right, top, width, bottom)
    }
}