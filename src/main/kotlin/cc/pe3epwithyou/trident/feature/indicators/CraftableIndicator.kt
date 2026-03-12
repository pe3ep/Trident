package cc.pe3epwithyou.trident.feature.indicators

import cc.pe3epwithyou.trident.config.Config
import cc.pe3epwithyou.trident.events.container.withContainerCtx
import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.Texture
import cc.pe3epwithyou.trident.utils.extensions.ItemStackExtensions.findInLore
import cc.pe3epwithyou.trident.utils.extensions.ItemStackExtensions.getLore
import cc.pe3epwithyou.trident.utils.minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.world.inventory.Slot

object CraftableIndicator {
    private val texture = Texture(
        Resources.trident("textures/interface/no_materials.png"),
        6,
        8,
    )

    fun render(graphics: GuiGraphics, slot: Slot) {
        if (!Config.Global.craftableIndicators) return
        fusion(graphics, slot)
        val slotName = slot.item.hoverName.string
        if ("Blueprint: " !in slotName) return
        val lore = slot.item.getLore().reversed()
        lore.forEach { line ->
            if ("Click to Assemble" !in line.string) return@forEach
            if ("(Missing materials)" !in line.string) return@forEach
            texture.blit(
                graphics,
                slot.x,
                slot.y + 8,
            )
            return
        }
    }

    fun fusion(graphics: GuiGraphics, slot: Slot) {
        val screen = (minecraft().screen as? ContainerScreen) ?: return
        withContainerCtx(screen) {
            requireTitle("FUSION FORGE")
            slot.item.findInLore(Regex(""". > Click to Forge \(Missing materials\)"""))?.let {
                texture.blit(
                    graphics,
                    slot.x,
                    slot.y + 8,
                )
            }
        }
    }
}