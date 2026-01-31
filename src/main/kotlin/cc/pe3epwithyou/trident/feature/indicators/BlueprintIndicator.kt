package cc.pe3epwithyou.trident.feature.indicators

import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.Texture
import cc.pe3epwithyou.trident.utils.extensions.ItemStackExtensions.findInLore
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.resources.Identifier
import net.minecraft.world.inventory.Slot

object BlueprintIndicator {
    @JvmStatic
    fun checkItem(graphics: GuiGraphics, slot: Slot) {
        if (Minecraft.getInstance().screen !is ContainerScreen) return
        checkLore(graphics, slot)
    }

    private fun checkLore(graphics: GuiGraphics, slot: Slot) {
        if (slot.item.isEmpty) return
        val slotName = slot.item.hoverName.string
        if ("Blueprint: " !in slotName) return
        slot.item.findInLore(Regex("""Royal Donations: (\d+)/(\d+)"""))?.let {
            val donatedAmount = it.groups[1]?.value?.toIntOrNull() ?: return
            val donatedTotal = it.groups[2]?.value?.toIntOrNull() ?: return
            if (donatedAmount == donatedTotal) {
                renderIcon(graphics, slot, Icons.MAXED_COSMETIC)
            }
        } ?: run {
            renderIcon(graphics, slot, Icons.NEW_COSMETIC)
        }
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

    private enum class Icons(val texturePath: Identifier) {
        NEW_COSMETIC(
            Resources.trident("textures/interface/blueprint_indicators/new.png")
        ),
        MAXED_COSMETIC(
            Resources.trident("textures/interface/blueprint_indicators/maxed.png")
        )
    }
}