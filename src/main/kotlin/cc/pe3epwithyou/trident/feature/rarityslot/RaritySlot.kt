package cc.pe3epwithyou.trident.feature.rarityslot

import cc.pe3epwithyou.trident.config.Config
import com.noxcrew.sheeplib.util.opacity
import com.noxcrew.sheeplib.util.opaqueColor
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.TextColor
import net.minecraft.world.inventory.Slot

object RaritySlot {
    private val ALLOWED_COLORS = listOf<TextColor>(
        TextColor.fromRgb(0xF94242),
        TextColor.fromRgb(0xFF8000),
        TextColor.fromRgb(0xA335EE),
        TextColor.fromRgb(0x0070DD),
        TextColor.fromRgb(0x1EFF00),
    )

    fun render(graphics: GuiGraphics, slot: Slot) {
        if (!Config.RaritySlot.enabled) return

        if (slot.item.isEmpty) return
        val color = slot.item.hoverName.toFlatList().firstOrNull()?.style?.color ?: return

        if (color !in ALLOWED_COLORS) return
        renderOutline(graphics, slot, color)
    }

    fun renderOutline(graphics: GuiGraphics, slot: Slot, color: TextColor) {
        val transparentColor = color.value opacity 0
        val opaqueColor = color.value.opaqueColor()
        val x = slot.x
        val y = slot.y

        if (Config.RaritySlot.displayType == DisplayType.OUTLINE) {
            graphics.fill(x, y, x + 1, y + 16, opaqueColor) // Left side
            graphics.fill(x, y, x + 16, y + 1, opaqueColor) // Top side
            graphics.fill(x + 15, y, x + 16, y + 16, opaqueColor) // Right side
            graphics.fill(x, y + 15, x + 16, y + 16, opaqueColor) // Bottom side
        }

        if (Config.RaritySlot.displayType == DisplayType.U_SHAPED) {
            graphics.fillGradient(x, y, x + 1, y + 16, transparentColor, opaqueColor) // Left side
            graphics.fillGradient(x + 15, y, x + 16, y + 16, transparentColor, opaqueColor) // Right side
            graphics.fill(x, y + 15, x + 16, y + 16, opaqueColor) // Bottom side
        }

        if (Config.RaritySlot.displayType == DisplayType.FILL) {
            graphics.fill(x, y, x + 16, y + 16, opaqueColor)
        }
    }
}