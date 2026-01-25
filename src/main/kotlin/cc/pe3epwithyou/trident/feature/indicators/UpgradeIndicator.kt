package cc.pe3epwithyou.trident.feature.indicators

import cc.pe3epwithyou.trident.utils.Resources
import cc.pe3epwithyou.trident.utils.Texture
import cc.pe3epwithyou.trident.utils.extensions.ItemStackExtensions.findInLore
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.world.inventory.Slot

object UpgradeIndicator {
    private val screens = listOf("FISHING PERKS", "STYLE PERKS")
    private val upgradeTexture = Texture(
        Resources.trident("textures/interface/upgrade_arrow.png"),
        7,
        8
    )
    private val lockedTexture = Texture(
        Resources.trident("textures/interface/upgrade_locked.png"),
        7,
        7
    )

    private fun checkUpgrade(screen: Screen, slot: Slot): Boolean {
        if (screens.any { it in screen.title.string }) {
            slot.item.findInLore(Regex(""". > Left-Click to Upgrade"""))?.let {
                return true
            }
        }
        return false
    }

    private fun checkLocked(screen: Screen, slot: Slot): Boolean {
        if (screens.any { it in screen.title.string }) {
            slot.item.findInLore(Regex("""Reach Style Level \d+ to unlock"""))?.let {
                return true
            }
        }
        return false
    }

    fun render(graphics: GuiGraphics, slot: Slot) {
        val screen = Minecraft.getInstance().screen ?: return
        if (checkUpgrade(screen, slot)) upgradeTexture.blit(graphics, slot.x - 1, slot.y + 9)
        if (screen.title.string == "STYLE PERKS" && checkLocked(screen, slot)) lockedTexture.blit(
            graphics,
            slot.x - 1,
            slot.y + 10
        )
    }
}