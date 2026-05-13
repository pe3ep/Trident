package cc.pe3epwithyou.trident.utils

import com.noxcrew.sheeplib.util.opaqueColor
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.Slot

object DebugDraw {
    fun renderSlotNumber(graphics: GuiGraphicsExtractor, slot: Slot) {
        val index = slot.index.toString()
        val x = slot.x
        val y = slot.y
        val font = minecraft().font
        graphics.text(
            font,
            Component.literal(index),
            x,
            y,
            0xFFFFFF.opaqueColor()
        )
    }
}