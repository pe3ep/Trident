package cc.pe3epwithyou.trident.config.screen

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.feature.rarityslot.DisplayType
import cc.pe3epwithyou.trident.feature.rarityslot.RaritySlot
import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.Texture
import com.noxcrew.sheeplib.util.opaqueColor
import dev.isxander.yacl3.gui.image.ImageRenderer
import net.minecraft.client.gui.GuiGraphicsExtractor

class RaritySlotPreview : ImageRenderer {
    companion object {
        var RARITY_DISPLAY_TYPE: DisplayType = Config.RaritySlot.displayType
    }

    override fun render(
        graphics: GuiGraphicsExtractor,
        x: Int,
        y: Int,
        renderWidth: Int,
        tickDelta: Float
    ): Int {
        val colors = RaritySlot.ALLOWED_COLORS
        val models = listOf(
            Resources.minecraft("textures/item/diamond.png"),
            Resources.minecraft("textures/item/redstone.png"),
            Resources.minecraft("textures/item/iron_ingot.png"),
            Resources.minecraft("textures/item/ender_pearl.png"),
            Resources.minecraft("textures/item/paper.png"),
        )
        val height = 128

        val slotWidth = 16
        val gap = 1
        val width = (colors.size * (slotWidth + gap)) - gap
        var startX = x + renderWidth / 2 - width / 2
        val startY = y + height / 2 - 8

        graphics.fill(x, y, x + renderWidth, y + height, 0x202020.opaqueColor())

        colors.forEachIndexed { index, color ->
            graphics.fill(
                startX,
                startY,
                startX + slotWidth,
                startY + slotWidth,
                0x404040.opaqueColor()
            )
            RaritySlot.renderOutline(graphics, startX, startY, color, RARITY_DISPLAY_TYPE)
            Texture(
                models[index],
                slotWidth,
                slotWidth
            ).blit(graphics, startX, startY)

            startX += slotWidth + gap
        }

        return height
    }

    override fun close() = Unit
}