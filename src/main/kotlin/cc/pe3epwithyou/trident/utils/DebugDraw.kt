package cc.pe3epwithyou.trident.utils

import com.noxcrew.sheeplib.util.opaqueColor
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.Slot

object DebugDraw {
    fun renderSlotNumber(graphics: GuiGraphics, slot: Slot) {
        val index = slot.index.toString()
        val x = slot.x
        val y = slot.y
        val font = Minecraft.getInstance().font
        graphics.drawString(
            font,
            Component.literal(index),
            x,
            y,
            0xFFFFFF.opaqueColor()
        )
    }
}