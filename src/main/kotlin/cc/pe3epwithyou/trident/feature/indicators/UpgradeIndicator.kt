package cc.pe3epwithyou.trident.feature.indicators

import cc.pe3epwithyou.trident.feature.rarityslot.RaritySlot
import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.Texture
import cc.pe3epwithyou.trident.utils.extensions.ItemStackExtensions.findInLore
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.TextColor
import net.minecraft.world.inventory.Slot

object UpgradeIndicator {
    private val screens = listOf("FISHING PERKS", "STYLE PERKS")
    private val texture = Texture(
        Resources.trident("textures/interface/upgrade_arrow.png"),
        7,
        8
    )

    private fun check(screen: Screen, slot: Slot): Boolean {
        if (screens.any { it in screen.title.string }) {
            slot.item.findInLore(Regex(""". > Left-Click to Upgrade"""))?.let {
                return true
            }
        }
        return false
    }

    fun render(graphics: GuiGraphics, slot: Slot) {
        val screen = Minecraft.getInstance().screen ?: return
        if (check(screen, slot)) texture.blit(graphics, slot.x - 1, slot.y - 1)
    }

    fun renderOutline(graphics: GuiGraphics, slot: Slot) {
        val screen = Minecraft.getInstance().screen ?: return
        if (check(screen, slot)) {
            RaritySlot.renderOutline(graphics, slot.x, slot.y, TextColor.fromRgb(0x1EFF00))
        }
    }
}