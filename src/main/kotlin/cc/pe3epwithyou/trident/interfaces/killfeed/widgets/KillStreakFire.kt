package cc.pe3epwithyou.trident.interfaces.killfeed.widgets

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.utils.Model
import cc.pe3epwithyou.trident.utils.Resources
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component

class KillStreakFire() : AbstractWidget(0, 0, 22, 10, Component.empty()) {
    private companion object {
        val FIRE_MODEL: Model = Model(
            Resources.trident("interface/rampage_fire"),
            22,
            10
        )
    }

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        if (!Config.KillFeed.showKillstreaks) {
            return
        }
        FIRE_MODEL.render(
            guiGraphics,
            x,
            y + 3
        )
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) = Unit

}