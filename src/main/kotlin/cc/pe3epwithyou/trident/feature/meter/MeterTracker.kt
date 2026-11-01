package cc.pe3epwithyou.trident.feature.meter

import cc.pe3epwithyou.trident.interfaces.hotbar.HotbarDecoration
import cc.pe3epwithyou.trident.utils.Model
import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.Texture
import cc.pe3epwithyou.trident.utils.extensions.ComponentExtensions.mccFont
import cc.pe3epwithyou.trident.utils.minecraft
import com.noxcrew.sheeplib.util.opacity
import com.noxcrew.sheeplib.util.opaqueColor
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component

object MeterTracker {
    private val DAILY_ICON = Texture(Resources.trident("textures/interface/meter/daily.png"), 9, 7)
    private val DAILY_ICON_GOLD = Resources.trident("interface/daily_gold_animated")

    fun register() {

    }

    fun getDecoration() = HotbarDecoration { graphics, _, x0, y0 ->
        val font = minecraft().font
        val text = Component.literal("2/8").mccFont().withoutShadow()

        val progress = 0.67f // TODO: Calculate the real progress %

        val width = font.width(text) + 5 + 2 + 9 + 2 + 4
        graphics.fill(x0, y0, x0 + width, y0 + 9, 0x000000 opacity 128)
        renderVerticalProgress(graphics, x0 + 2, y0 + 2, progress)
        val iconX = x0 + 2 + 5 + 2
        val iconY = y0 + 1
        if (progress == 1f) {
            Model(DAILY_ICON_GOLD, 9, 7).render(
                graphics, iconX, iconY
            )
        } else {
            DAILY_ICON.blit(graphics, iconX, iconY)
        }
        graphics.text(font, text, x0 + 2 + 5 + 2 + 9 + 2, y0, 0xFFFFFF.opaqueColor())

        width
    }

    fun renderVerticalProgress(graphics: GuiGraphicsExtractor, x0: Int, y0: Int, progress: Float) {
        val f = progress.coerceIn(0f, 1f)
        fun color(i: Float): Int = if (f < i) 0x686969.opaqueColor() else 0xffba28.opaqueColor()

        graphics.fill(x0, y0, x0 + 5, y0 + 1, color(1.0f))
        graphics.fill(x0, y0 + 1, x0 + 5, y0 + 2, color(0.8f))
        graphics.fill(x0, y0 + 2, x0 + 5, y0 + 3, color(0.6f))
        graphics.fill(x0, y0 + 3, x0 + 5, y0 + 4, color(0.4f))
        graphics.fill(x0, y0 + 4, x0 + 5, y0 + 5, color(0.2f))
    }
}