package cc.pe3epwithyou.trident.feature

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.Texture
import cc.pe3epwithyou.trident.utils.extensions.ItemStackExtensions.getLore
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.inventory.Slot

object CraftableIndicator {
    fun render(graphics: GuiGraphics, slot: Slot) {
        if (!Config.Global.craftableIndicators) return
        val slotName = slot.item.hoverName.string
        if ("Blueprint: " !in slotName) return
        val lore = slot.item.getLore()
        val lastLine = lore.last().string
        if ("Click to Assemble" !in lastLine) return
        if ("(Missing materials)" !in lastLine) return
        Texture(
            Resources.trident("textures/interface/no_materials.png"),
            6,
            8,
        ).blit(
            graphics,
            slot.x,
            slot.y + 8,
        )
    }
}