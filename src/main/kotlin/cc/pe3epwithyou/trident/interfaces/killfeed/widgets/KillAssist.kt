package cc.pe3epwithyou.trident.interfaces.killfeed.widgets

import cc.pe3epwithyou.trident.utils.NoxesiumUtils
import cc.pe3epwithyou.trident.utils.extensions.GraphicsExtensions.fillRoundedAll
import com.noxcrew.sheeplib.util.opaqueColor
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component

class KillAssist(val color: Int) : AbstractWidget(0, 0, 13, 9, Component.empty()) {
    private val skullComponent: Component

    init {
        val client = Minecraft.getInstance()
        val uuid = client.gameProfile.id
        skullComponent = NoxesiumUtils.skullComponent(
            uuid = uuid, scale = 0.75f
        )
    }

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {
        guiGraphics.fillRoundedAll(
            x, y + 6, 11, 9, color
        )
        val font = Minecraft.getInstance().font
        guiGraphics.drawString(font, skullComponent, x + 2, y + 7, 0xFFFFFF.opaqueColor())
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput): Unit = Unit
}