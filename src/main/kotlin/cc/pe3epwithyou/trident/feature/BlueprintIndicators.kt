package cc.pe3epwithyou.trident.feature

import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.Texture
import cc.pe3epwithyou.trident.utils.extensions.ItemStackExtensions.getLore
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.Slot

object BlueprintIndicators {
    fun checkLore(graphics: GuiGraphics, slot: Slot) {
        if (slot.item.isEmpty) return
        val slotName = slot.item.hoverName.string
        if ("Blueprint: " !in slotName) return
        val lore = slot.item.getLore()
        var isNew = true
        lore.forEach { line ->
            if ("Royal Donations" !in line.string) return@forEach
            isNew = false
            val donations = line.string.split(": ")[1]
            val donatedAmount = donations.split("/")[0].toInt()
            val donatedTotal = donations.split("/")[1].toInt()
            if (donatedAmount == donatedTotal) {
                renderIcon(graphics, slot, Icons.MAXED_COSMETIC)
                return
            }
        }
        if (!isNew) return
        renderIcon(graphics, slot, Icons.NEW_COSMETIC)
    }

    private fun renderIcon(graphics: GuiGraphics, slot: Slot, icon: Icons) {
        val x = slot.x
        val y = slot.y
        val texture = Texture(
            icon.texturePath,
            10,
            10,
        )
        texture.blit(graphics, x + 8, y - 2)
    }

    private enum class Icons(val texturePath: ResourceLocation) {
        NEW_COSMETIC(
            Resources.trident("textures/interface/blueprint_indicators/new.png")
        ),
        MAXED_COSMETIC(
            Resources.trident("textures/interface/blueprint_indicators/maxed.png")
        )
    }
}