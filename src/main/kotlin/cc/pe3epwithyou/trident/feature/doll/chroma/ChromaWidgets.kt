package cc.pe3epwithyou.trident.feature.doll.chroma

import cc.pe3epwithyou.trident.feature.doll.DollCosmetics
import cc.pe3epwithyou.trident.feature.doll.DollCosmetics.isWeaponSkin
import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.Texture
import cc.pe3epwithyou.trident.utils.extensions.GraphicsExtensions.fillRoundedAll
import cc.pe3epwithyou.trident.utils.gridLayout
import cc.pe3epwithyou.trident.utils.playMaster
import com.noxcrew.sheeplib.CompoundWidget
import com.noxcrew.sheeplib.util.opacity
import com.noxcrew.sheeplib.util.opaqueColor
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.sounds.SoundManager
import net.minecraft.network.chat.Component

class ChromaWidgets(x: Int, y: Int) : CompoundWidget(x, y, 0, 0) {
    override val layout = gridLayout(1) {
        var col = 0
        var row = 0
        Chroma.entries.forEach { chroma ->
            ChromaWidget(chroma).at(row, col)
            col++
            if (col == 5) {
                col = 0
                row++
            }
        }
    }

    override fun renderWidget(graphics: GuiGraphics, i: Int, j: Int, f: Float) {
        val item =
            DollCosmetics.currentCosmetics[DollCosmetics.CosmeticType.SKIN]?.slot?.item ?: return
        if (!isWeaponSkin(item)) return

        val notSelected = Component.literal("Select Chroma").withStyle(ChatFormatting.GRAY)
        val selected = DollCosmetics.currentChroma?.chromaName?.let { Component.literal(it) }

        graphics.drawCenteredString(
            Minecraft.getInstance().font,
            selected ?: notSelected,
            (x + width / 2),
            y - 14,
            0xffffff.opaqueColor()
        )
        graphics.fillRoundedAll(x - 1, y - 1, width + 2, height + 2, 0x111111 opacity 128)
        super.renderWidget(graphics, i, j, f)
    }

    init {
        layout.arrangeElements()
        layout.visitWidgets(this::addChild)

        width = layout.width
        height = layout.height

        layout.x = x
        layout.y = y
    }

    class ChromaWidget(val chroma: Chroma) : AbstractWidget(0, 0, 14, 14, Component.empty()) {
        val isSelected: Boolean
            get() = DollCosmetics.currentChroma == chroma

        val texture = Texture(
            chroma.itemTexture,
            10, 10, 16, 16
        )

        override fun renderWidget(
            graphics: GuiGraphics,
            i: Int,
            j: Int,
            f: Float
        ) {
            val item = DollCosmetics.currentCosmetics[DollCosmetics.CosmeticType.SKIN]?.slot?.item
                ?: return
            if (!isWeaponSkin(item)) return
            graphics.fillRoundedAll(
                x, y, 14, 14, when {
                    isSelected -> 0xffffff opacity 96
                    isHovered -> 0xffffff opacity 32
                    else -> 0xffffff opacity 0
                }
            )

            texture.blit(graphics, x + 2, y + 2)
        }

        override fun onClick(mouseButtonEvent: MouseButtonEvent, bl: Boolean) {
            val item = DollCosmetics.currentCosmetics[DollCosmetics.CosmeticType.SKIN]?.slot?.item
                ?: return
            if (!isWeaponSkin(item)) return
            if (DollCosmetics.currentChroma == chroma) {
                DollCosmetics.currentChroma = null
                return
            }

            DollCosmetics.currentChroma = chroma
        }

        override fun playDownSound(soundManager: SoundManager) {
            val item = DollCosmetics.currentCosmetics[DollCosmetics.CosmeticType.SKIN]?.slot?.item
                ?: return
            if (!isWeaponSkin(item)) return
            soundManager.playMaster(Resources.mcc("ui.click_normal"))
        }

        override fun isFocused(): Boolean = false

        override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) = Unit

    }
}