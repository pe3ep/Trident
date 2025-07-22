package cc.pe3epwithyou.trident.feature

import com.noxcrew.sheeplib.util.opaqueColor
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics

import net.minecraft.network.chat.TextColor
import net.minecraft.world.inventory.Slot

object RaritySlot {
    fun render(graphics: GuiGraphics, slot: Slot) {
        val x = slot.x
        val y = slot.y

        if (slot.item.isEmpty) return
//        Don't show the background for common items to reduce visual clutter
        if (slot.item.hoverName.style.color == null) return
        if (slot.item.hoverName.style.color == TextColor.fromLegacyFormat(ChatFormatting.WHITE)) return

        graphics.fill(x, y + 15, x + 16, y + 16, 10, slot.item.hoverName.style.color!!.value.opaqueColor())
        graphics.fill(x, y, x + 16, y + 1, 10, slot.item.hoverName.style.color!!.value.opaqueColor())

        graphics.fill(x, y, x + 1, y + 16, 10, slot.item.hoverName.style.color!!.value.opaqueColor())
        graphics.fill(x + 15, y, x + 16, y + 16, 10, slot.item.hoverName.style.color!!.value.opaqueColor())
    }

}