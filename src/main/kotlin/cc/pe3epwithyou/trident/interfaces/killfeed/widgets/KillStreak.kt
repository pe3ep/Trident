package cc.pe3epwithyou.trident.interfaces.killfeed.widgets

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.Texture
import cc.pe3epwithyou.trident.utils.extensions.GraphicsExtensions.fillRoundedAll
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

class KillStreak(
    private val color: Int,
    private val streak: Int
) : AbstractWidget(0, 0, 15, 9, Component.empty()) {
    private fun getStreakTexture(): Texture {
        val coercedStreak = streak.coerceIn(1, 5)
        return Texture(
            Resources.trident("textures/interface/killfeed/streaks/streak$coercedStreak.png"),
            13,
            9
        )
    }

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        if (!Config.KillFeed.showKillstreaks || streak < 2) {
            return
        }
        guiGraphics.fillRoundedAll(
            x,
            y + 6,
            13,
            9,
            color
        )
        getStreakTexture().blit(guiGraphics, x, y + 6)
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) = Unit

}