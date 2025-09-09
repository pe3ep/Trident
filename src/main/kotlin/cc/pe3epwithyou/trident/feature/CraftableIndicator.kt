package cc.pe3epwithyou.trident.feature

import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.Texture
import cc.pe3epwithyou.trident.utils.extensions.ItemStackExtensions.getLore
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.inventory.Slot

object CraftableIndicator {
    fun render(graphics: GuiGraphics, slot: Slot) {
        val slotName = slot.item.hoverName.string
        if ("Blueprint: " !in slotName) return
        val lastLine = slot.item.getLore().last().string
        if ("Click to Assemble" !in lastLine) return
        if ("(Missing materials)" in lastLine) return
        Texture(
            Resources.mcc("textures/island_interface/assembler/tab/fusion/crafting_station/available.png"),
            12,
            12,
            16,
            16
        ).blit(
            graphics,
            slot.x - 1,
            slot.y + 4,
        )
    }
}